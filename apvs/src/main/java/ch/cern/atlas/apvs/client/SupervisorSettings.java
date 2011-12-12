package ch.cern.atlas.apvs.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;

/**
 * 
 * @author duns
 * 
 */
public class SupervisorSettings implements Serializable {

	private static final long serialVersionUID = -1921805477855090343L;

	public static final String[] workerSettingNames = { "Name", "PTU Id",
			"Dosimeter #", "URL Helmet Camera", "URL Hand Camera", "Add/Remove" };
	@SuppressWarnings("rawtypes")
	public static final Class[] workerCellClass = { EditTextCell.class,
			SelectionCell.class, SelectionCell.class, EditTextCell.class,
			EditTextCell.class, ButtonCell.class };
	@SuppressWarnings("rawtypes")
	public static final Class[] workerNameClass = { TextCell.class, TextCell.class,
			TextCell.class, TextCell.class, TextCell.class, ButtonCell.class };

	private Map<String, Map<String, String>> settings;
	private Map<String, List<Map<String, String>>> workerSettings;

	public SupervisorSettings() {
		this(false);
	}

	public SupervisorSettings(boolean useDefaults) {
		workerSettings = new HashMap<String, List<Map<String, String>>>();

		if (useDefaults) {
			put(Settings.DEFAULT_SUPERVISOR, 0, workerSettingNames[0], "Mark Donszelmann");
			put(Settings.DEFAULT_SUPERVISOR, 0, workerSettingNames[3], "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8");
			put(Settings.DEFAULT_SUPERVISOR, 0, workerSettingNames[4], "http://quicktime.tc.columbia.edu/users/lrf10/movies/sixties.mov");
		}
	}
	
	public String get(String supervisor, String name) {
		if (supervisor == null) {
			return null;
		}

		Map<String, String> map = settings.get(supervisor);
		if (map == null) {
			return null;
		}

		return map.get(name);
	}
	
	public String put(String supervisor, String name, String value) {
		if (supervisor == null) {
			return null;
		}
		
		Map<String, String> map = settings.get(supervisor);
		if (map == null) {
			map = new HashMap<String, String>();
			settings.put(supervisor, map);
		}

		return map.put(name, value);
	}

	public String get(String supervisor, int index, String name) {
		if (supervisor == null) {
			return null;
		}
		
		List<Map<String, String>> list = workerSettings.get(supervisor);
		if (list == null) {
			return null;
		}

		Map<String, String> map = list.get(index);
		if (map == null) {
			return null;
		}

		return map.get(name);
	}

	public String put(String supervisor, int index, String name, String value) {
		if (supervisor == null) {
			return null;
		}
		
		List<Map<String, String>> list = workerSettings.get(supervisor);
		if (list == null) {
			list = new ArrayList<Map<String, String>>();
			workerSettings.put(supervisor, list);
		}

		Map<String, String> map = index < list.size() ? list.get(index) : null;
		if (map == null) {
			for (int i = 0; i < index - list.size() + 1; i++) {
				list.add(new HashMap<String, String>());
				System.err.println(index + " " + list.size());
			}
			map = new HashMap<String, String>();
			list.set(index, map);
		}

		return map.put(name, value);
	}

	public void remove(String supervisor, int index) {
		if (supervisor == null) {
			return;
		}

		List<Map<String, String>> list = workerSettings.get(supervisor);
		if (list == null) {
			return;
		}

		list.remove(index);
	}

	public int size(String supervisor) {
		if (supervisor == null) {
			return 0;
		}

		List<Map<String, String>> list = workerSettings.get(supervisor);
		if (list == null) {
			return 0;
		}

		return list.size();
	}

	public String getName(String supervisor, int ptuId) {
		int index = getIndexForPtu(supervisor, ptuId);
		if (index < 0)
			return null;

		return get(supervisor, index, workerSettingNames[0]);
	}
	
	public void setName(String supervisor, Integer ptuId, String value) {
		int index = getIndexForPtu(supervisor, ptuId);
		if (index < 0)
			return;
		
		put(supervisor, index, workerSettingNames[0], value);
	}


	public String getHelmetCameraUrl(String supervisor, int ptuId) {
		int index = getIndexForPtu(supervisor, ptuId);
		if (index < 0)
			return null;

		return get(supervisor, index, workerSettingNames[3]);
	}

	public String getHandCameraUrl(String supervisor, int ptuId) {
		int index = getIndexForPtu(supervisor, ptuId);
		if (index < 0)
			return null;

		return get(supervisor, index, workerSettingNames[4]);
	}

	public String debugString() {
		StringBuffer s = new StringBuffer("Supervisor Settings {\n");
		int index = 0;
		for (Iterator<String> j = workerSettings.keySet().iterator(); j.hasNext();) {
			String supervisor = j.next();
			s.append("  " + supervisor + "\n");
			List<Map<String, String>> list = workerSettings.get(supervisor);
			for (Iterator<Map<String, String>> i = list.iterator(); i.hasNext();) {
				Map<String, String> entry = i.next();
				s.append("    " + index + "\n");
				for (Iterator<String> k = entry.keySet().iterator(); k
						.hasNext();) {
					String key = k.next();
					s.append("    " + key + ": " + entry.get(key) + "\n");
				}
				index++;
			}
		}
		s.append("}");
		return s.toString();
	}

	private int getIndexForPtu(String supervisor, int ptuId) {
		if (supervisor == null) {
			return -1;
		}
		
		List<Map<String, String>> list = workerSettings.get(supervisor);
		if (list == null) {
			return -1;
		}

		int i = 0;
		while (i < list.size()) {
			String value = list.get(i).get(workerSettingNames[1]);
			if ((value != null) && (Integer.parseInt(value) == ptuId)) {
				break;
			}
			i++;
		}

		return i < list.size() ? i : -1;
	}

	public Iterator<Map<String, String>> iterator(String supervisor) {
		if (supervisor == null) {
			return new ArrayList<Map<String, String>>().iterator();
		}
		
		List<Map<String, String>> list = workerSettings.get(supervisor);
		return list != null ? list.iterator()
				: new ArrayList<Map<String, String>>().iterator();
	}
}
