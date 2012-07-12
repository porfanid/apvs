package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author duns
 * 
 */
public class PtuSettings implements Serializable {

	private static final long serialVersionUID = -5390424254145424045L;

	private Map<Integer, Entry> entries = new HashMap<Integer, Entry>();

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

	public HashMap<Integer, Integer> getDosimeterToPtuMap() {
		HashMap<Integer, Integer> dosimeterToPtu = new HashMap<Integer, Integer>();

		// takes the last proper value
		for (Iterator<Integer> i = entries.keySet().iterator(); i
				.hasNext();) {
			Integer ptuId = i.next();
			Integer serialNo = entries.get(ptuId).dosimeterSerialNo;
			if ((ptuId != null) && (serialNo != null)) {
				dosimeterToPtu.put(serialNo, ptuId);
			}
		}

		return dosimeterToPtu;
	}

	public Boolean isEnabled(Integer object) {
		return entries.get(object).enabled;
	}
	
	public void setEnabled(Integer object, Boolean value) {
		entries.get(object).enabled = value;
	}

	public String getName(Integer object) {
		return entries.get(object).name;
	}

	public void setName(Integer object, String value) {
		entries.get(object).name = value;
	}

	public Integer getDosimeterSerialNumber(Integer object) {
		return entries.get(object).dosimeterSerialNo;
	}
	
	public void setDosimeterSerialNumber(Integer object, Integer value) {
		entries.get(object).dosimeterSerialNo = value;
	}

	public String getHelmetUrl(Integer object) {
		return entries.get(object).helmetUrl;
	}
	
	public void setHelmetUrl(Integer object, String value) {
		entries.get(object).helmetUrl = value;
	}

	public String getHandUrl(Integer object) {
		return entries.get(object).handUrl;
	}
	
	public void setHandUrl(Integer object, String value) {
		entries.get(object).handUrl = value;
	}


	public List<Integer> getPtuIds() {
		List<Integer> list = new ArrayList<Integer>();
		list.addAll(entries.keySet());
		return list;
	}

	public boolean add(Integer ptuId) {
		System.err.println("Adding "+ptuId);
		if (!entries.containsKey(ptuId)) {
			entries.put(ptuId, new Entry());
			return true;
		}
		return false;
	}

	// Returns ptuId associated to docimeterSerialNo
	public Integer getPtuId(Integer dosimeterSerialNo) {
		return getDosimeterToPtuMap().get(dosimeterSerialNo);
	}
}
