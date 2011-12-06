package ch.cern.atlas.apvs.client;

import java.util.HashMap;
import java.util.Map;

public class Setting {
	
	private Map<String, Object> settings = new HashMap<String, Object>();
	
	public Setting(int id) {
		settings.put("Name", "Person"+id);
	}
	
	public Object getSetting(String name) {
		return settings.get(name);
	}

}
