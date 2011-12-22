package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.net.Socket;
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

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;
import ch.cern.atlas.apvs.ptu.shared.PtuChangedEvent;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.cedarsoftware.util.io.JsonReader;

public class PtuReader implements Runnable {

	private RemoteEventBus eventBus;
	private Socket socket;
	private SortedMap<Integer, Ptu> ptus;
	private boolean ready = false;
	
	private boolean ptuIdsChanged = false;
	private Map<Integer, Set<String>> measurementChanged = new HashMap<Integer, Set<String>>();

	public PtuReader(final RemoteEventBus eventBus, Socket socket) {
		this.eventBus = eventBus;
		this.socket = socket;
		init();

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();

				if (type.equals(PtuIdsChangedEvent.class.getName())) {
					eventBus.fireEvent(new PtuIdsChangedEvent(getPtuIds()));
				} else if (type.equals(PtuChangedEvent.class.getName())) {
					for (Iterator<Integer> i = ptus.keySet().iterator(); i
							.hasNext();) {
						eventBus.fireEvent(new PtuChangedEvent(getPtu(i.next())));
					}
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

	private void init() {
		ptus = new TreeMap<Integer, Ptu>();
	}

	@Override
	public void run() {

		try {
			JsonReader reader = new PtuJsonReader(socket.getInputStream());

			// set a flag after 10 seconds, to not send events out when history comes in. 
			// FIXME we should have a better way to handle a lot of changes coming in. 
			// InputStream.ready() does NOT work. Maybe something with non-blocking sockets
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// ignored
					}
					sendEvents();
				}
			}).start();

			while (true) {
				Object object = reader.readObject();

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
					ptu.addMeasurement(measurement);
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
		} catch (IOException e) {
			System.err.println(getClass() + " " + e);
		} finally {
			close();
		}
	}

	private synchronized void sendEvents() {
		int t = 0;
		// fire all at the end
		// FIXME we can still add MeasurementNamesChanged
		if (ptuIdsChanged) {
			eventBus.fireEvent(new PtuIdsChangedEvent(new ArrayList<Integer>(
					ptus.keySet())));
			ptuIdsChanged = false;
			t++;
		}

		for (Iterator<Integer> i = measurementChanged.keySet().iterator(); i
				.hasNext();) {
			eventBus.fireEvent(new PtuChangedEvent(ptus.get(i.next())));
			t++;
		}

		for (Iterator<Integer> i = measurementChanged.keySet().iterator(); i
				.hasNext();) {
			int id = i.next();
			for (Iterator<String> j = measurementChanged.get(id).iterator(); j
					.hasNext();) {
				Measurement<Double> m = ptus.get(id).getMeasurement(j.next());
				eventBus.fireEvent(new MeasurementChangedEvent(m));
				t++;
			}
		}

		measurementChanged.clear();

		System.err.println("Fired " + t);

		ready = true;
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// ignored
		}
		System.err.println("Closed PTU socket, invalidated PTUids");
		init();
		eventBus.fireEvent(new PtuIdsChangedEvent(null));
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
