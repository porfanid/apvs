package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioSettings implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String, Entry> entries = new HashMap<String, Entry>();

	public static class Entry implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String  username;
		String  number;
		String  destUser;
		String  channel;
		String  activity;
		String  status;
		
		// Private Call
		Boolean onCall;
		
		// Conference Call
		String  room;
		Boolean onConference;
		
		
		public Entry() {
			username = "";
			number = "";
			channel = "";
			destUser = "";
			status = "";
			onCall = false;
			activity = "";
		}

		public String toString() {
			return ("VoipAccount: username=" + username + "" + " number= "
					+ number + " channel=" + channel + " destUser= " + destUser
					+ " status=" + status + " onCall=" + onCall);
		}
	}

	public AudioSettings() {
	}

	// Username Methods
	public String getUsername(String ptuId) {
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.username : "");
	}

	public boolean setUsername(String ptuId, String username) {
		if (entries.get(ptuId).username.equals(username)) {
			return false;
		}
		entries.get(ptuId).username = username;
		return true;
	}

	// Number Methods
	public String getNumber(String ptuId) {
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.number : "");
	}

	public void setNumber(String ptuId, String number) {
		entries.get(ptuId).number = number;
	}

	// Channel Methods
	public String getChannel(String ptuId) {
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.channel : "");
	}

	public void setChannel(String ptuId, String channel) {
		entries.get(ptuId).channel = channel;
	}

	// Destination User Methods
	public String getDestUser(String ptuId) {
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.destUser : "");
	}

	public void setDestUser(String ptuId, String user) {
		entries.get(ptuId).destUser = user;
	}

	// Status Methods
	public String getStatus(String ptuId) {
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.status : "");
	}

	public void setStatus(String ptuId, String status) {
		entries.get(ptuId).status = status;
	}

	// On Call Status Methods
	public Boolean getOnCall(String ptuId) {
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.onCall : false);
	}

	public void setOnCall(String ptuId, Boolean onCall) {
		entries.get(ptuId).onCall = onCall;
	}

	// Activity Methods
	public String getActivity(String ptuId) {
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.activity : "");
	}

	public void setActivity(String ptuId, String activity) {
		entries.get(ptuId).activity = activity;
	}

	// PTU Ids
	public List<String> getPtuIds() {
		List<String> list = new ArrayList<String>();
		list.addAll(entries.keySet());
		return list;
	}

	// Add PTU
	public boolean add(String ptuId) {
		System.err.println("Adding " + ptuId);
		if (!entries.containsKey(ptuId)) {
			entries.put(ptuId, new Entry());
			return true;
		}
		return false;
	}

	// Check PTU is in the list
	public boolean contains(String ptuId) {
		if (entries.containsKey(ptuId)) {
			return true;
		}
		return false;
	}

	// Check PTU ID of given number
	public String getPtuId(AudioSettings accounts, String number) {
		List<String> ptuList = new ArrayList<String>(accounts.getPtuIds());
		for (int i = 0; i < ptuList.size(); i++) {
			if (accounts.getNumber(ptuList.get(i)).equals(number)) {
				return ptuList.get(i);
			}
		}
		return null;
	}
}
