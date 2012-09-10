package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.client.ui.CameraView;

/**
 * 
 * @author duns
 * 
 */
public class PtuSettings implements Serializable {

	private static final long serialVersionUID = -5390424254145424045L;

	private Map<String, Entry> entries = new HashMap<String, Entry>();

	public static class Entry implements Serializable {
		private static final long serialVersionUID = 1L;

		String name;
		Boolean enabled;
		Integer dosimeterSerialNo;
		String helmetUrl;
		String handUrl;

		public Entry() {
			name = "";
			enabled = true;
			dosimeterSerialNo = 0;
			helmetUrl = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
			handUrl = "http://quicktime.tc.columbia.edu/users/lrf10/movies/sixties.mov";
		}
	}

	public PtuSettings() {
	}

	public HashMap<Integer, String> getDosimeterToPtuMap() {
		HashMap<Integer, String> dosimeterToPtu = new HashMap<Integer, String>();

		// takes the last proper value
		for (Iterator<String> i = entries.keySet().iterator(); i.hasNext();) {
			String ptuId = i.next();
			Integer serialNo = entries.get(ptuId).dosimeterSerialNo;
			if ((ptuId != null) && (serialNo != null)) {
				dosimeterToPtu.put(serialNo, ptuId);
			}
		}

		return dosimeterToPtu;
	}

	public Boolean isEnabled(String object) {
		return entries.get(object).enabled;
	}

	public void setEnabled(String object, Boolean value) {
		entries.get(object).enabled = value;
	}

	public String getName(String object) {
		return entries.get(object).name;
	}

	public void setName(String object, String value) {
		entries.get(object).name = value;
	}

	public Integer getDosimeterSerialNumber(String object) {
		return entries.get(object).dosimeterSerialNo;
	}

	public void setDosimeterSerialNumber(String object, Integer value) {
		entries.get(object).dosimeterSerialNo = value;
	}

	public String getCameraUrl(String ptuId, String type) {
		return type.equals(CameraView.HELMET) ? entries.get(ptuId).helmetUrl
				: entries.get(ptuId).handUrl;
	}

	public void setCameraUrl(String ptuId, String type, String value) {
		if (type.equals(CameraView.HELMET)) {
			entries.get(ptuId).helmetUrl = value;
		} else {
			entries.get(ptuId).handUrl = value;
		}
	}

	public List<String> getPtuIds() {
		List<String> list = new ArrayList<String>();
		list.addAll(entries.keySet());
		return list;
	}

	public boolean add(String ptuId) {
		System.err.println("Adding " + ptuId);
		if (!entries.containsKey(ptuId)) {
			entries.put(ptuId, new Entry());
			return true;
		}
		return false;
	}

	// Returns ptuId associated to dosimeterSerialNo
	public String getPtuId(Integer dosimeterSerialNo) {
		return getDosimeterToPtuMap().get(dosimeterSerialNo);
	}
}
