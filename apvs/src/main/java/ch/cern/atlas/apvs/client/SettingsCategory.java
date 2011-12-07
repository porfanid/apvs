package ch.cern.atlas.apvs.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.web.bindery.autobean.shared.AutoBean;

public class SettingsCategory  {

	private static Map<Integer, Map<String, String>> getMap(AutoBean<Settings> instance) {
		Map<Integer, Map<String, String>> settings = instance.as().getMap();
		if (settings == null) {
			settings = new HashMap<Integer, Map<String, String>>();
			instance.as().setMap(settings);
		}
		return settings;
	}
	
	public static String get(AutoBean<Settings> instance, int id, String name) {
		Map<Integer, Map<String, String>> settings = getMap(instance);
		Map<String, String> setting = settings.get(id);	
		if (setting == null) return null;
		return setting.get(name);
	}

	public static void put(AutoBean<Settings> instance, int id, String name, String value) {
		Map<Integer, Map<String, String>> settings = getMap(instance);
		Map<String, String> setting = settings.get(id);
		if (setting == null) {
			setting = new HashMap<String, String>();
			setting.put("Name", "Person "+id);
			settings.put(id, setting);
		}
		setting.put(name, value);
	}

	public static int size(AutoBean<Settings> instance) {
		return instance.as().getMap().size();
	}

	public static String debugString(AutoBean<Settings> instance) {
		StringBuffer s = new StringBuffer("Settings {\n");
		for (Iterator<Map.Entry<Integer,Map<String,String>>> i = instance.as().getMap().entrySet().iterator(); i.hasNext();) {
			Entry<Integer, Map<String, String>> entry = i.next();
			s.append("  "+entry.getKey());
			for (Iterator<Map.Entry<String, String>> j = entry.getValue().entrySet().iterator(); j.hasNext(); ) {
				Entry<String, String> subEntry = j.next();
				s.append("    "+subEntry.getKey()+": "+subEntry.getValue());
			}
		}
		s.append("}");
		return s.toString();
	}
}
