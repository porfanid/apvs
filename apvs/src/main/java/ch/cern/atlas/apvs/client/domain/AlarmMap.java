package ch.cern.atlas.apvs.client.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.domain.Alarm;


public class AlarmMap implements Serializable {


	private static final long serialVersionUID = -7761956767282031317L;

	private Map<String, Alarm> alarms  = new HashMap<String,Alarm>();
	
	public AlarmMap() {
		// serializable
	}
	
	public Alarm get(String ptuId) {
		Alarm alarm = alarms.get(ptuId);
		if (alarm == null) {
			put(new Alarm(ptuId));
		}
		return alarms.get(ptuId);
	}
	
	public Alarm put(Alarm alarm) {
		String ptuId = alarm.getPtuId();
		return alarms.put(ptuId, alarm);
	}
	
	public boolean isPanic(String ptuId) {
		return get(ptuId).isPanic();
	}
	
	public boolean isDose(String ptuId) {
		return get(ptuId).isDose();
	}
	
	public boolean isFall(String ptuId) {
		return get(ptuId).isFall();
	}
	
	public void setPanic(String ptuId, boolean state) {
		get(ptuId).setPanic(state);
	}

	public void setDose(String ptuId, boolean state) {
		get(ptuId).setDose(state);
	}

	public void setFall(String ptuId, boolean state) {
		get(ptuId).setFall(state);
	}
	
	public List<String> getPtuIds() {
		List<String> ptuIds = new ArrayList<String>(alarms.keySet());
		Collections.sort(ptuIds);
		return ptuIds;
	}
		
	public void clear() {
		alarms.clear();
	}


	@Override
	public String toString() {
		return "AlarmMap: "+alarms.size();
	}

}
