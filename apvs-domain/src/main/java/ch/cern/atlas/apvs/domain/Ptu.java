package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Ptu implements Serializable, IsSerializable {
	
	private static final long serialVersionUID = 1933500417755260216L;
	
	private Device device;
	protected Map<String, Measurement> measurements = new HashMap<String, Measurement>();
		
	public Ptu() {
		device = new Device(null);
	}
	
	public Ptu(Device device) {
		this.device = device;
	}
	
	@Override
	public int hashCode() {
		return device.hashCode() + measurements.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if ((obj != null) && (obj instanceof Ptu)) {
			Ptu p = (Ptu)obj;
			return (p.device.equals(device)) && (p.getMeasurements().equals(measurements));
		}
		return super.equals(obj);
	}
			
	public String getPtuId() {
		return device.getName();
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
			
	public Measurement addMeasurement(Measurement measurement) throws APVSException {
		String name = measurement.getName();
		Measurement r = measurements.get(name);
		
		// check if we try to store an older measurement
		if ((r != null) && (r.getDate().getTime() > measurement.getDate().getTime())) {
			throw new APVSException("addMeasurement out of order for "+device.getName()+" "+measurement.getName());
		} else {
			measurements.put(name, measurement);
		}
		
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

	public Device getDevice() {
		return device;
	}	
}

