package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;


//NOTE: implements IsSerializable in case serialization file cannot be found
public class InterventionMap implements Serializable, IsSerializable {

	private static final long serialVersionUID = 8868971785801918119L;

	private Map<Device, Intervention> interventions = new HashMap<Device, Intervention>();
	
	public InterventionMap() {
		// serializable
	}
	
	// For Audio...
	public Intervention get(String name) {
		for (Device device : interventions.keySet()) {
			if (device.getName().equals(name)) {
				return get(device);
			}
		}
		return null;
	}
	
	public Intervention get(Device device) {
		return interventions.get(device);
	}
	
	public Intervention put(Device device, Intervention intervention) {
		return interventions.put(device, intervention);
	}

	public List<Device> getPtus() {
		List<Device> ptus = new ArrayList<Device>(interventions.keySet());
		Collections.sort(ptus);
		return ptus;
	}

	public void clear() {
		interventions.clear();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InterventionMap) {
			return interventions.entrySet().equals(((InterventionMap)obj).interventions.entrySet());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return interventions.entrySet().hashCode();
	}

}
