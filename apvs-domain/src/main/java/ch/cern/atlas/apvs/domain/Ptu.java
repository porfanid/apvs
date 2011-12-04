package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Ptu implements Serializable {
	
	private static final long serialVersionUID = 1933500417755260216L;

	private int ptuId;
	protected Map<String, Measurement<Double>> measurements = new HashMap<String, Measurement<Double>>();
		
	public Ptu() {
		ptuId = -1;
	}
	
	public Ptu(int ptuId) {
		this.ptuId = ptuId;
	}
	
	@Override
	public int hashCode() {
		return ptuId + measurements.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if ((obj != null) && (obj instanceof Ptu)) {
			Ptu p = (Ptu)obj;
			return (p.ptuId == ptuId) && (p.getMeasurements().equals(measurements));
		}
		return super.equals(obj);
	}
			
	public int getPtuId() {
		return ptuId;
	}
	
	public List<String> getMeasurementNames() {
		return new ArrayList<String>(measurements.keySet());
	}
	
	public int getSize() {
		return measurements.size();
	}
	
	public Measurement<Double> getMeasurement(String name) {
		return measurements.get(name);
	}
	
	public Measurement<Double> setMeasurement(String name, Measurement<Double> measurement) {
		return measurements.put(name, measurement);
	}
	
	public Measurement<Double> add(Measurement<Double> measurement) {
		// FIXME check ptuId
		return setMeasurement(measurement.getName(), measurement);
	}

	public Collection<? extends Measurement<Double>> getMeasurements() {
		return measurements.values();
	}
}

