package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class Ptu implements Serializable {
	
	private static final long serialVersionUID = 1933500417755260216L;
	private final Logger log = Logger.getLogger(getClass().getName());
	
	private String ptuId;
	protected Map<String, Measurement> measurements = new HashMap<String, Measurement>();
	protected Map<String, History> histories = new HashMap<String, History>();
		
	public Ptu() {
		ptuId = null;
	}
	
	public Ptu(String ptuId) {
		this.ptuId = ptuId;
	}
	
	@Override
	public int hashCode() {
		return ptuId.hashCode() + measurements.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if ((obj != null) && (obj instanceof Ptu)) {
			Ptu p = (Ptu)obj;
			return (p.ptuId == ptuId) && (p.getMeasurements().equals(measurements));
		}
		return super.equals(obj);
	}
			
	public String getPtuId() {
		return ptuId;
	}
	
	public List<String> getMeasurementNames() {
		return new ArrayList<String>(measurements.keySet());
	}
	
	public int getSize() {
		return measurements.size();
	}
	
	public Measurement getMeasurement(String name) {
		return measurements.get(name);
	}
			
	public Measurement addMeasurement(Measurement measurement) {
		String name = measurement.getName();
		Measurement r = measurements.get(name);
		// FIXME move history and add measurement
		
		// check if we try to store an older measurement
		if ((r != null) && (r.getDate().getTime() > measurement.getDate().getTime())) {
			log.warning("addMeasurement out of order for "+ptuId+" "+measurement.getName());
		} else {
			measurements.put(name, measurement);
		}
		
		// FIXME limit size
		/*
		Integer limitNoOfValues = this.limitNoOfValues.get(measurement.getName());
		while ((limitNoOfValues != null) && (m.size() > limitNoOfValues)) {
			m.remove(0);
		}
		*/
		return r;
	}

	public Collection<? extends Measurement> getMeasurements() {
		List<Measurement> r = new ArrayList<Measurement>(measurements.size());
		for (Iterator<String> i = measurements.keySet().iterator(); i.hasNext(); ) {
			Measurement m = getMeasurement(i.next());
			if (m != null) r.add(m);
		}
		return r;
	}
	
	public History getHistory(String name) {
		return histories.get(name);
	}
	
	public History setHistory(String sensor, History history) {
		return histories.put(sensor, history);
	}	
}

