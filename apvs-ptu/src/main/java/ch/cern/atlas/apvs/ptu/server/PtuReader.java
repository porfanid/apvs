package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
	private SortedMap<Integer, Ptu> ptus = new TreeMap<Integer, Ptu>();

	public PtuReader(final RemoteEventBus eventBus, Socket socket) {
		this.eventBus = eventBus;
		this.socket = socket;
		
		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {
			
			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();
				
				if (type.equals(PtuIdsChangedEvent.class.getName())) {
					eventBus.fireEvent(new PtuIdsChangedEvent(getPtuIds()));
				} else if (type.equals(PtuChangedEvent.class.getName())) {
					for (Iterator<Integer> i = ptus.keySet().iterator(); i.hasNext(); ) {
						eventBus.fireEvent(new PtuChangedEvent(getPtu(i.next())));
					}
				} else if (type.equals(MeasurementChangedEvent.class.getName())) {
					List<Measurement<Double>> m = getMeasurements();
					System.err.println("Getting all meas "+m.size());
					for (Iterator<Measurement<Double>> i = m.iterator(); i.hasNext(); ) {
						eventBus.fireEvent(new MeasurementChangedEvent(i.next()));
					}
				}
			}
		});
	}

	@Override
	public void run() {

		try {
			JsonReader reader = new PtuJsonReader(socket.getInputStream());
			while (true) {
				Object object = reader.readObject();
				if (object instanceof Measurement<?>) {
					@SuppressWarnings("unchecked")
					Measurement<Double> measurement = (Measurement<Double>) object;

					boolean ptuIdsChanged = false;
					int ptuId = measurement.getPtuId();
					Ptu ptu = ptus.get(ptuId);
					if (ptu == null) {
						ptu = new Ptu(ptuId);
						ptus.put(ptuId, ptu);
						ptuIdsChanged = true;
					}
					ptu.add((Measurement<Double>) measurement);

					// fire all at the end
					// FIXME we can still add MeasurementNamesChanged
					if (ptuIdsChanged) {
						eventBus.fireEvent(new PtuIdsChangedEvent(
								new ArrayList<Integer>(ptus.keySet())));
					}
					eventBus.fireEvent(new PtuChangedEvent(ptu));
					eventBus.fireEvent(new MeasurementChangedEvent(measurement));
				}
			}
		} catch (IOException e) {
			System.err.println(getClass() + " " + e);
		} finally {
			close();
		}
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// ignored
		}
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
