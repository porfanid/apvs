package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ch.cern.atlas.apvs.client.domain.Intervention;

public class InterventionMap implements Serializable {

	private static final long serialVersionUID = 8868971785801918119L;

	private Map<String, Intervention> interventions = new HashMap<String, Intervention>();
	
	public InterventionMap() {
		// serializable
	}
	
	public Intervention get(String ptuId) {
		return interventions.get(ptuId);
	}
	
	public Intervention put(String ptuId, Intervention intervention) {
		return interventions.put(ptuId, intervention);
	}

	public Set<String> getPtuIds() {
		return interventions.keySet();
	}

	public void clear() {
		interventions.clear();
	}
	

}
