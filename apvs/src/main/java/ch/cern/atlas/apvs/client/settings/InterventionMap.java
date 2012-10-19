package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import ch.cern.atlas.apvs.client.domain.Intervention;
import ch.cern.atlas.apvs.client.event.InterventionMapChangedEvent;

public class InterventionMap implements Serializable {

	private static final long serialVersionUID = 8868971785801918119L;

	private SortedMap<String, Intervention> interventions = new TreeMap<String, Intervention>();
	
	public InterventionMap() {
		// serializable
	}
	
	public Intervention get(String ptuId) {
		return interventions.get(ptuId);
	}
	
	public Intervention put(String ptuId, Intervention intervention) {
		return interventions.put(ptuId, intervention);
	}

	public List<String> getPtuIds() {
		return new ArrayList<String>(interventions.keySet());
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
