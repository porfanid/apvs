package ch.cern.atlas.apvs.server;

import java.util.HashMap;
import java.util.Map;

public class SensorMap {

	private Map<String, Boolean> enabled;
	
	public SensorMap() {
		enabled = new HashMap<String, Boolean>();
	}
	
	public void setEnabled(String ptuId, String sensor, boolean enabled) {
		this.enabled.put(getKey(ptuId, sensor), enabled);
	}

	public boolean isEnabled(String ptuId, String sensor) {
		Boolean b = enabled.get(getKey(ptuId, sensor));
		return b == null ? true : b;
	}
	
	private String getKey(String ptuId, String sensor) {
		return ptuId+":"+sensor;
	}
}
