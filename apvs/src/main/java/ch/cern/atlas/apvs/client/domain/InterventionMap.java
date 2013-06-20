package ch.cern.atlas.apvs.client.domain;

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

	public List<String> getPtuIds() {
		List<String> ptuIds = new ArrayList<String>(interventions.keySet());
		Collections.sort(ptuIds);
		return ptuIds;
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
