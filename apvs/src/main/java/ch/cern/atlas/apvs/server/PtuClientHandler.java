package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

import ch.cern.atlas.apvs.domain.Error;
import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.domain.Report;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.server.PtuConstants;
import ch.cern.atlas.apvs.ptu.server.PtuJsonReader;
import ch.cern.atlas.apvs.ptu.server.PtuReconnectHandler;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

public class PtuClientHandler extends PtuReconnectHandler {

	private static final Logger logger = Logger
			.getLogger(PtuClientHandler.class.getName());
	private final RemoteEventBus eventBus;

	private Ptus ptus = Ptus.getInstance();

	private Map<String, Set<String>> measurementChanged = new HashMap<String, Set<String>>();

	public PtuClientHandler(ClientBootstrap bootstrap,
			final RemoteEventBus eventBus) {
		super(bootstrap);
		this.eventBus = eventBus;

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();

				if (type.equals(MeasurementChangedEvent.class.getName())) {
					List<Measurement> m = getMeasurements();
					System.err.println("Getting all meas " + m.size());
					for (Iterator<Measurement> i = m.iterator(); i.hasNext();) {
						eventBus.fireEvent(new MeasurementChangedEvent(i.next()));
					}
				}
			}
		});
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		// Print out the line received from the server.
		String line = (String) e.getMessage();
		// System.err.println(line);

		List<Message> list;
		try {
			list = PtuJsonReader.jsonToJava(line);
			for (Iterator<Message> i = list.iterator(); i.hasNext();) {
				Message message = i.next();
				String ptuId = message.getPtuId();
				Ptu ptu = ptus.get(ptuId);
				if (ptu != null) {
					if (message instanceof Measurement) {
						handleMessage(ptu, (Measurement) message);
					} else if (message instanceof Report) {
						handleMessage(ptu, (Report) message);
					} else if (message instanceof Event) {
						handleMessage(ptu, (Event) message);
					} else if (message instanceof Error) {
						handleMessage(ptu, (Error) message);
					} else {
						System.err.println("Error: unknown Message Type: "
								+ message.getType());
					}
				}
			}
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}

	}

	private void handleMessage(Ptu ptu, Measurement message) {

		String ptuId = message.getPtuId();
		String sensor = message.getName();
		History history = ptus.setHistory(ptuId, sensor, message.getUnit());
		
		ptu.addMeasurement(message);
		Set<String> changed = measurementChanged.get(ptuId);
		if (changed == null) {
			changed = new HashSet<String>();
			measurementChanged.put(ptuId, changed);
		}
		changed.add(message.getName());

		// duplicate entries will not be added
		if (history != null) {
			history.addEntry(message.getDate().getTime(), message.getValue());
		}

		sendEvents();
	}

	private void handleMessage(Ptu ptu, Report report) {
		System.err.println(report.getType() + " NOT YET IMPLEMENTED");
	}

	private void handleMessage(Ptu ptu, Event event) {
		System.err.println(event.getType() + " NOT YET IMPLEMENTED");
	}

	private void handleMessage(Ptu ptu, Error error) {
		System.err.println(error.getType() + " NOT YET IMPLEMENTED");
	}

	private synchronized void sendEvents() {

		for (Iterator<String> i = measurementChanged.keySet().iterator(); i
				.hasNext();) {
			String id = i.next();
			for (Iterator<String> j = measurementChanged.get(id).iterator(); j
					.hasNext();) {
				Measurement m = ptus.get(id).getMeasurement(j.next());
				eventBus.fireEvent(new MeasurementChangedEvent(m));
			}
		}

		measurementChanged.clear();
	}

	public Ptu getPtu(String ptuId) {
		return ptus.get(ptuId);
	}

	public List<String> getPtuIds() {
		List<String> list = new ArrayList<String>();
		list.addAll(ptus.getPtuIds());
		Collections.sort(list);
		return list;
	}

	public List<Measurement> getMeasurements() {
		List<Measurement> m = new ArrayList<Measurement>();
		for (Iterator<Ptu> i = ptus.getPtus().iterator(); i.hasNext();) {
			m.addAll(i.next().getMeasurements());
		}
		return m;
	}
}
