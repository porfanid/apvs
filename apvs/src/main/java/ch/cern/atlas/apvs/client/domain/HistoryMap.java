package ch.cern.atlas.apvs.client.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.Measurement;


public class HistoryMap implements Serializable {

	private static final long serialVersionUID = -1943029642235865794L;

	private Map<String, Map<String, History>> histories  = new HashMap<String, Map<String,History>>();
	private Map<String, String> units = new HashMap<String, String>();	
	
	public HistoryMap() {
		// serializable
	}
	
	public History get(String ptuId, String name) {
		 Map<String, History> ptu = getHistories(ptuId);
		 if (ptu == null) {
			 return null;
		 }
		return ptu.get(name);
	}
	
	public History put(History history) {
		String ptuId = history.getPtuId();
		Map<String, History> ptu = getHistories(ptuId);
		if (ptu == null) {
			ptu = new HashMap<String, History>();
			histories.put(ptuId, ptu);
		}
		String name = history.getName();
		units.put(name, history.getUnit());
		return ptu.put(name, history);
	}
	
	public List<String> getPtuIds() {
		List<String> ptuIds = new ArrayList<String>(histories.keySet());
		Collections.sort(ptuIds);
		return ptuIds;
	}
		
	public Map<String, History> getHistories(String ptuId) {
		return histories.get(ptuId);
	}

	public void clear() {
		histories.clear();
	}

	public Set<Measurement> getMeasurements(String ptuId) {
		Map<String, History> ptu = getHistories(ptuId);
		if (ptu == null) {
			return Collections.emptySet();
		}
		
		Set<Measurement> s = new HashSet<Measurement>(ptu.size());
		for (Entry<String, History> entry : ptu.entrySet()) {
			s.add(entry.getValue().getMeasurement());
		}
		return s;
	}

	public Measurement getMeasurement(String ptuId, String name) {
		Map<String, History> ptu = getHistories(ptuId);
		if (ptu == null) {
			return null;
		}
		
		History history = ptu.get(name);
		if (history == null) {
			return null;
		}
		
		return history.getMeasurement();
	}
	
	public String getDisplayName(String name) {
		return Measurement.getDisplayName(name);
	}

	public String getUnit(String name) {
		return units.get(name);
	}

	@Override
	public String toString() {
		return "HistoryMap: "+histories.size();
	}
}
