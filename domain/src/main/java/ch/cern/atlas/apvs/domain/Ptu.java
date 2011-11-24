package ch.cern.atlas.apvs.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Ptu {
	
	// SHould be MAP FIXME
	private int ptuId;
	Map<String, Measurement<Double>> measurements = new HashMap<String, Measurement<Double>>();

	public Ptu(int ptuId) {
		this.ptuId = ptuId;
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
	
	public void setMeasurement(String name, Measurement<Double> measurement) {
		measurements.put(name, measurement);
	}
	
	public void add(Measurement<Double> measurement) {
		// FIXME check ptuId
		measurements.put(measurement.getName(), measurement);
	}
}
