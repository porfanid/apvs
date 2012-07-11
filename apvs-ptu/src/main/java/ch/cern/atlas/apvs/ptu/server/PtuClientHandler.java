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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

public class PtuClientHandler extends SimpleChannelUpstreamHandler {

	private static final int RECONNECT_DELAY = 20;
	private final Timer timer = new HashedWheelTimer();

	private static final Logger logger = Logger
			.getLogger(PtuClientHandler.class.getName());
	private final ClientBootstrap bootstrap;
	private final RemoteEventBus eventBus;
	
	private SortedMap<Integer, Ptu> ptus;
	private boolean ready = false;

	private boolean ptuIdsChanged = false;
	private Map<Integer, Set<String>> measurementChanged = new HashMap<Integer, Set<String>>();
	private Channel channel;

	public PtuClientHandler(ClientBootstrap bootstrap, final RemoteEventBus eventBus) {
		this.bootstrap = bootstrap;
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
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		channel = e.getChannel();
		super.channelConnected(ctx, e);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		System.err.println("Closed PTU socket, invalidated PTUids");
		init();
		eventBus.fireEvent(new PtuIdsChangedEvent(new ArrayList<Integer>()));

		System.err.println("Sleeping for: " + RECONNECT_DELAY + "s");
		timer.newTimeout(new TimerTask() {
			public void run(Timeout timeout) throws Exception {
				System.err.println("Reconnecting to PTU");
				connect();
			}
		}, RECONNECT_DELAY, TimeUnit.SECONDS);

		super.channelClosed(ctx, e);
		channel = null;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		// Print out the line received from the server.
		System.err.println(e.getMessage());
		Object object = PtuJsonReader.toJava((String) e.getMessage());
		if (object instanceof Measurement<?>) {
			@SuppressWarnings("unchecked")
			Measurement<Double> measurement = (Measurement<Double>) object;

			int ptuId = measurement.getPtuId();
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

			if (ready) {
				sendEvents();
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.log(Level.WARNING, "Unexpected exception from downstream.",
				e.getCause());
		e.getChannel().close();
	}
	
	public ChannelFuture connect() {
		ChannelFuture future = disconnect();
		if (future != null) {
			future.addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					bootstrap.connect();
				}
			});
			return future;
		}
		return bootstrap.connect();
	}
	
	public ChannelFuture disconnect() {
		if (channel != null) {
			return channel.disconnect();
		}
		return null;
	}

	private void init() {
		ptus = new TreeMap<Integer, Ptu>();
	}

	private synchronized void sendEvents() {
		// fire all at the end
		// FIXME we can still add MeasurementNamesChanged
		if (ptuIdsChanged) {
			eventBus.fireEvent(new PtuIdsChangedEvent(new ArrayList<Integer>(
					ptus.keySet())));
			ptuIdsChanged = false;
		}

		for (Iterator<Integer> i = measurementChanged.keySet().iterator(); i
				.hasNext();) {
			int id = i.next();
			for (Iterator<String> j = measurementChanged.get(id).iterator(); j
					.hasNext();) {
				Measurement<Double> m = ptus.get(id).getMeasurement(j.next());
				eventBus.fireEvent(new MeasurementChangedEvent(m));
			}
		}

		measurementChanged.clear();

		ready = true;
	}

	public Ptu getPtu(int ptuId) {
		return ptus.get(ptuId);
	}

	public List<Integer> getPtuIds() {
		List<Integer> list = new ArrayList<Integer>(ptus.keySet());
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
