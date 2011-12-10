package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.web.bindery.autobean.shared.AutoBean;

public class SettingsCategory {
		
	public static String get(AutoBean<Settings> instance, int index, String name) {
		List<Map<String, String>> list = instance.as().getList();
		if (list == null) return null;
		Map<String, String> map = list.get(index);	
		if (map == null) return null;
		return map.get(name);
	}

	public static void put(AutoBean<Settings> instance, int index, String name, String value) {
		List<Map<String, String>> list = instance.as().getList();
		if (list == null) {
			list = new ArrayList<Map<String, String>>();
			instance.as().setList(list);
		}

		Map<String, String> map = index < list.size() ? list.get(index) : null;
		if (map == null) {
			for (int i=0; i < index - list.size() + 1; i++) {
				list.add(new HashMap<String, String>());
				System.err.println(index +" "+list.size());
			}
			map = new HashMap<String, String>();
			list.set(index, map);
		}
		
		map.put(name, value);
	}
	
	public static void remove(AutoBean<Settings> instance, int index) {
		List<Map<String, String>> list = instance.as().getList();
		if (list == null) return;
		list.remove(index);
	}

	public static int size(AutoBean<Settings> instance) {
		List<Map<String, String>> list = instance.as().getList();
		return list != null ? list.size() : 0;
	}
	
	public static String getName(AutoBean<Settings> instance, int ptuId) {
		int index = getIndexForPtu(instance, ptuId);
		if (index < 0) return null;
		
		return get(instance, index, SettingsView.settingNames[0]);
	}
	
	public static String getHelmetCameraUrl(AutoBean<Settings> instance, int ptuId) {
		int index = getIndexForPtu(instance, ptuId);
		if (index < 0) return null;
		
		return get(instance, index, SettingsView.settingNames[3]);
	}

	public static String getHandCameraUrl(AutoBean<Settings> instance, int ptuId) {
		int index = getIndexForPtu(instance, ptuId);
		if (index < 0) return null;
		
		return get(instance, index, SettingsView.settingNames[4]);
	}

	public static String debugString(AutoBean<Settings> instance) {
		StringBuffer s = new StringBuffer("Settings {\n");
		int index = 0;
		for (Iterator<Map<String,String>> i = instance.as().getList().iterator(); i.hasNext();) {
			Map<String, String> entry = i.next();
			s.append("  "+index+"\n");
			for (Iterator<String> j = entry.keySet().iterator(); j.hasNext(); ) {
				String key = j.next();
				s.append("    "+key+": "+entry.get(key)+"\n");
			}
			index++;
		}
		s.append("}");
		return s.toString();
	}
	
	private static int getIndexForPtu(AutoBean<Settings> instance, int ptuId) {
		List<Map<String, String>> list = instance.as().getList();
		if (list == null) return -1;
		
		int i = 0;
		while (i < list.size()) {
			String value = list.get(i).get(SettingsView.settingNames[1]);
			if ((value != null) && (Integer.parseInt(value) == ptuId)) {
				break;
			}
				i++;
		}
		
		return i < list.size() ? i : -1;
	}
}
