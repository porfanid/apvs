package ch.cern.atlas.apvs.client.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.domain.Alarm;
import ch.cern.atlas.apvs.domain.Device;

import com.google.gwt.user.client.rpc.IsSerializable;


//NOTE: implements IsSerializable in case serialization file cannot be found
public class AlarmMap implements Serializable, IsSerializable {


	private static final long serialVersionUID = -7761956767282031317L;

	private Map<Device, Alarm> alarms  = new HashMap<Device, Alarm>();
	
	public AlarmMap() {
		// serializable
	}
	
	public Alarm get(Device device) {
		Alarm alarm = alarms.get(device);
		if (alarm == null) {
			put(new Alarm(device));
		}
		return alarms.get(device);
	}
	
	public Alarm put(Alarm alarm) {
		Device device = alarm.getPtu();
		return alarms.put(device, alarm);
	}
	
	public boolean isPanic(Device device) {
		return get(device).isPanic();
	}
	
	public boolean isDose(Device device) {
		return get(device).isDose();
	}
	
	public boolean isFall(Device device) {
		return get(device).isFall();
	}
	
	public void setPanic(Device device, boolean state) {
		get(device).setPanic(state);
	}

	public void setDose(Device device, boolean state) {
		get(device).setDose(state);
	}

	public void setFall(Device device, boolean state) {
		get(device).setFall(state);
	}
	
	public List<Device> getPtus() {
		List<Device> ptus = new ArrayList<Device>(alarms.keySet());
		Collections.sort(ptus);
		return ptus;
	}
		
	public void clear() {
		alarms.clear();
	}


	@Override
	public String toString() {
		
		StringBuffer s = new StringBuffer("AlarmMap: ");
		s.append(alarms.size());
		for (Iterator<Alarm> i = alarms.values().iterator(); i.hasNext(); ) {
			s.append(", ");
			s.append(i.next());
		}
		return s.toString(); 
		
	}

}
