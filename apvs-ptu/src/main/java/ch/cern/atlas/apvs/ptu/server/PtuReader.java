package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;

import com.cedarsoftware.util.io.JsonReader;
import com.google.web.bindery.event.shared.EventBus;

public class PtuReader implements Runnable {

	private EventBus eventBus;
	private Socket socket;
	private SortedMap<Integer, Ptu> ptus = new TreeMap<Integer, Ptu>();
	
	public PtuReader(EventBus eventBus, Socket socket) {
		this.eventBus = eventBus;
		this.socket = socket;
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
					ptu.add((Measurement<Double>)measurement);
					
					// fire all at the end
					// FIXME we can still add MeasurementNamesChanged
					if (ptuIdsChanged) {
						eventBus.fireEvent(new PtuIdsChangedEvent(new ArrayList<Integer>(ptus.keySet())));
					}
					eventBus.fireEvent(new PtuChangedEvent(ptu));
					eventBus.fireEvent(new MeasurementChangedEvent(measurement));
				}
			}
		} catch (IOException e) {
			System.err.println(getClass() + " " + e);
		} finally {
			try {
				close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	public void close() throws IOException {
		socket.close();
	}

	public Ptu getPtu(int ptuId) {
		return ptus.get(ptuId);
	}
	
	public SortedSet<Integer> getPtuIds() {
		return new TreeSet<Integer>(ptus.keySet());
	}

	public List<Measurement<Double>> getMeasurements() {
		List<Measurement<Double>> m = new ArrayList<Measurement<Double>>();
		for (Iterator<Ptu> i = ptus.values().iterator(); i.hasNext();) {
			m.addAll(i.next().getMeasurements());
		}
		return m;
	}
}
