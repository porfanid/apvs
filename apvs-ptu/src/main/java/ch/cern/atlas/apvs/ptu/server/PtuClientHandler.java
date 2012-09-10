package ch.cern.atlas.apvs.ptu.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

public class PtuClientHandler extends PtuReconnectHandler {

	private static final Logger logger = Logger
			.getLogger(PtuClientHandler.class.getName());
	private final RemoteEventBus eventBus;

	private SortedMap<String, Ptu> ptus;

	private boolean ptuIdsChanged = false;
	private Map<String, Set<String>> measurementChanged = new HashMap<String, Set<String>>();


	public PtuClientHandler(ClientBootstrap bootstrap,
			final RemoteEventBus eventBus) {
		super(bootstrap);
		this.eventBus = eventBus;

		init();

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();

				if (type.equals(PtuIdsChangedEvent.class.getName())) {
					eventBus.fireEvent(new PtuIdsChangedEvent(getPtuIds()));
				} else if (type.equals(MeasurementChangedEvent.class.getName())) {
					List<Measurement<Double>> m = getMeasurements();
					System.err.println("Getting all meas " + m.size());
					for (Iterator<Measurement<Double>> i = m.iterator(); i
							.hasNext();) {
						eventBus.fireEvent(new MeasurementChangedEvent(i.next()));
					}
				}
			}
		});
	}

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
			throws Exception {
		if (e instanceof ChannelStateEvent) {
			logger.info(e.toString());
		}
		super.handleUpstream(ctx, e);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// handle closed connection
		init();
		eventBus.fireEvent(new PtuIdsChangedEvent(new ArrayList<String>()));

		super.channelClosed(ctx, e);		
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		// Print out the line received from the server.
		String line = (String)e.getMessage();
//		System.err.println(line);
		Object object = PtuJsonReader.toJava(line);
		if (object instanceof Measurement<?>) {
			@SuppressWarnings("unchecked")
			Measurement<Double> measurement = (Measurement<Double>) object;

			String ptuId = measurement.getPtuId();
			Ptu ptu = ptus.get(ptuId);
			if (ptu == null) {
				ptu = new Ptu(ptuId);
				ptus.put(ptuId, ptu);
				ptuIdsChanged = true;
			}
			// FIXME, limit should come from server ???
			ptu.addMeasurement(measurement, PtuSimulator.limitNumberOfValues);
			Set<String> changed = measurementChanged.get(ptuId);
			if (changed == null) {
				changed = new HashSet<String>();
				measurementChanged.put(ptuId, changed);
			}
			changed.add(measurement.getName());

			sendEvents();
		}
	}

	private void init() {
		ptus = new TreeMap<String, Ptu>();
	}

	private synchronized void sendEvents() {
		// fire all at the end
		// FIXME we can still add MeasurementNamesChanged
		if (ptuIdsChanged) {
			eventBus.fireEvent(new PtuIdsChangedEvent(new ArrayList<String>(
					ptus.keySet())));
			ptuIdsChanged = false;
		}

		for (Iterator<String> i = measurementChanged.keySet().iterator(); i
				.hasNext();) {
			String id = i.next();
			for (Iterator<String> j = measurementChanged.get(id).iterator(); j
					.hasNext();) {
				Measurement<Double> m = ptus.get(id).getMeasurement(j.next());
				eventBus.fireEvent(new MeasurementChangedEvent(m));
			}
		}

		measurementChanged.clear();
	}

	public Ptu getPtu(String ptuId) {
		return ptus.get(ptuId);
	}

	public List<String> getPtuIds() {
		List<String> list = new ArrayList<String>(ptus.keySet());
		Collections.sort(list);
		return list;
	}

	public List<Measurement<Double>> getMeasurements() {
		List<Measurement<Double>> m = new ArrayList<Measurement<Double>>();
		for (Iterator<Ptu> i = ptus.values().iterator(); i.hasNext();) {
			m.addAll(i.next().getMeasurements());
		}
		return m;
	}
}
