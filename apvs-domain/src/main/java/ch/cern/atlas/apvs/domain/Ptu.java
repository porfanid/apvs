package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Ptu implements Serializable {
	
	private static final long serialVersionUID = 1933500417755260216L;

	private int ptuId;
	protected Map<String, List<Measurement<Double>>> measurements = new HashMap<String, List<Measurement<Double>>>();
	protected Map<String, Integer> limitNoOfValues = new HashMap<String, Integer>();
		
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
		List<Measurement<Double>> m = measurements.get(name);
		if ((m == null) || m.size() == 0) return null;
		return m.get(m.size()-1);
	}
	
	public List<Measurement<Double>> getMeasurements(String name) {
		return measurements.get(name);
	}
	
	public int getNumberOfMeasurements(String name) {
		List<Measurement<Double>> m = measurements.get(name);
		if ((m == null) || m.size() == 0) return 0;
		return m.size();
	}

	public Measurement<Double> getMeasurement(String name, int index) {
		List<Measurement<Double>> m = measurements.get(name);
		if ((m == null) || m.size() == 0) return null;
		return m.get(index);
	}
	
	public Measurement<Double> addMeasurement(Measurement<Double> measurement, int limitNoOfValues) {
		this.limitNoOfValues.put(measurement.getName(), limitNoOfValues);
		return addMeasurement(measurement);
	}
	
	public Measurement<Double> addMeasurement(Measurement<Double> measurement) {
		List<Measurement<Double>> m = measurements.get(measurement.getName());
		Measurement<Double> r;
		if (m == null) {
			m = new LinkedList<Measurement<Double>>();
			measurements.put(measurement.getName(), m);
			r = null;
		} else {
			r = m.size() > 0 ? m.get(m.size()-1) : null;
		}
		m.add(measurement);

		// limit size
		Integer limitNoOfValues = this.limitNoOfValues.get(measurement.getName());
		while ((limitNoOfValues != null) && (m.size() > limitNoOfValues)) {
			m.remove(0);
		}
		return r;
	}

	public Collection<? extends Measurement<Double>> getMeasurements() {
		List<Measurement<Double>> r = new ArrayList<Measurement<Double>>(measurements.size());
		for (Iterator<String> i = measurements.keySet().iterator(); i.hasNext(); ) {
			Measurement<Double> m = getMeasurement(i.next());
			if (m != null) r.add(m);
		}
		return r;
	}
}

