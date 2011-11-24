package ch.cern.atlas.apvs.domain;

import java.util.ArrayList;
import java.util.List;


public class Ptu {
	
	// SHould be MAP FIXME
	private int ptuId;
	List<Measurement<Double>> measurements = new ArrayList<Measurement<Double>>();

	public Ptu(int ptuId) {
		this.ptuId = ptuId;
	}
			
	public int getPtuId() {
		return ptuId;
	}
	
	public List<Measurement<Double>> getMeasurements() {
		return measurements;
	}
	
	public Measurement<Double> getMeasurement(int index) {
		return measurements.get(index);
	}
	
	public void setMeasurement(int index, Measurement<Double> measurement) {
		measurements.set(index, measurement);
	}
	
	public void add(Measurement<Double> measurement) {
		measurements.add(measurement);
	}
}
