package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.client.ui.CameraView;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author duns
 * 
 */
//NOTE: implements IsSerializable in case serialization file cannot be found
public class PtuSettings implements Serializable, IsSerializable {

	private static final long serialVersionUID = -5390424254145424045L;
	private static final String videoServer = "pcatlaswpss02";

	private Map<String, Entry> entries = new HashMap<String, Entry>();

	//NOTE: implements IsSerializable in case serialization file cannot be found
	public static class Entry implements Serializable, IsSerializable {
		private static final long serialVersionUID = 1L;

		Boolean enabled;
		String dosimeterSerialNo;
		String helmetUrl;
		String handUrl;
		String bssid;

		public Entry() {
			// Serializable
		}

		public Entry(String name) {
			enabled = true;
			dosimeterSerialNo = "";
			String id = Integer.toHexString(Integer.parseInt(name.substring(name.length()-1, name.length())));
			helmetUrl = "http://"+videoServer+":8"+id+"90/worker"+id+".mjpg";
			handUrl = "http://"+videoServer+":8"+id+"91/worker"+id+".mjpg";
			bssid = "";
		}

		@Override
		public String toString() {
			return "Entry [enabled=" + enabled + ", dosimeterSerialNo="
					+ dosimeterSerialNo + ", helmetUrl=" + helmetUrl
					+ ", handUrl=" + handUrl + ", bssid=" + bssid + "]";
		}

	}

	public PtuSettings() {
	}

	public HashMap<String, String> getDosimeterToPtuMap() {
		HashMap<String, String> dosimeterToPtu = new HashMap<String, String>();

		// takes the last proper value
		for (Iterator<String> i = entries.keySet().iterator(); i.hasNext();) {
			String name = i.next();
			String serialNo = entries.get(name).dosimeterSerialNo;
			if ((name != null) && (serialNo != null)) {
				dosimeterToPtu.put(serialNo, name);
			}
		}

		return dosimeterToPtu;
	}

	public Boolean isEnabled(String name) {
		Entry entry = entries.get(name);
		return entry != null ? entry.enabled : false;
	}

	public void setEnabled(String name, Boolean value) {
		entries.get(name).enabled = value;
	}

	public String getDosimeterSerialNumber(String name) {
		Entry entry = entries.get(name);
		return entry != null ? entry.dosimeterSerialNo : "";
	}

	public void setDosimeterSerialNumber(String name, String value) {
		entries.get(name).dosimeterSerialNo = value;
	}

	public String getCameraUrl(String name, String type, Proxy proxy) {
		Entry entry = entries.get(name);
		return entry != null ? type.equals(CameraView.HELMET) ? proxy.getReverseUrl(entry.helmetUrl)
				: proxy.getReverseUrl(entry.handUrl) : "";
	}

	public void setCameraUrl(String name, String type, String value, Proxy proxy) {
		if (type.equals(CameraView.HELMET)) {
			entries.get(name).helmetUrl = proxy.getUrl(value);
		} else {
			entries.get(name).handUrl = proxy.getUrl(value);
		}
	}

	public List<String> getPtuIds() {
		List<String> list = new ArrayList<String>();
		list.addAll(entries.keySet());
		return list;
	}

	public boolean add(String name) {
		if (!entries.containsKey(name)) {
			entries.put(name, new Entry(name));
			return true;
		}
		return false;
	}

	// Returns ptuId associated to dosimeterSerialNo
	public String getPtuId(String dosimeterSerialNo) {
		return getDosimeterToPtuMap().get(dosimeterSerialNo);
	}

	public void setBSSID(String name, String bssid) {
		entries.get(name).bssid = bssid;
	}
	
	public String getBSSID(String name) {
		Entry entry = entries.get(name);
		return entry != null ? entry.bssid : "";
	}
}
