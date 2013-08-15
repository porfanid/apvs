package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.domain.Intervention;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class AudioSettings implements Serializable, IsSerializable {

	private static final long serialVersionUID = 1L;
	private Map<String, VoipAccount> entries = new HashMap<String, VoipAccount>();

	public AudioSettings() {
	}

	/*********************************************
	 * Methods
	 *********************************************/
	// Username Methods
	public String getUsername(String ptuId) {
		Intervention intervention = getIntervention(ptuId);
		return (intervention != null ? intervention.getName() : "");
	}

	public boolean setUsername(String name, String username) {
		if (entries.get(name).getUsername().equals(username)) {
			return false;
		}
		entries.get(name).setUsername(username);
		return true;
	}

	
	// Intervention Methods
	public Intervention getIntervention(String ptuId) {
		VoipAccount entry = entries.get(ptuId);
		return (entry != null ? entry.getIntervention() : null);
	}

	public boolean setIntervention(String ptuId, Intervention intervention) {
		if (intervention.equals(getIntervention(ptuId))) {
			return false;
		}
		entries.get(ptuId).setIntervention(intervention);
		return true;
	}
	
	
	// Number Methods
	public String getNumber(String name) {
		VoipAccount entry = entries.get(name);
		return (entry != null ? entry.getAccount() != null ? entry.getAccount() : "" : "");
	}

	public void setNumber(String name, String number) {
		entries.get(name).setAccount(number);
	}

	
	// Channel Methods
	public String getChannel(String name) {
		VoipAccount entry = entries.get(name);
		return (entry != null ? entry.getChannel() : "");
	}

	public void setChannel(String name, String channel) {
		entries.get(name).setChannel(channel);
	}

	
	// Destination User Methods
	public String getDestUser(String name) {
		VoipAccount entry = entries.get(name);
		return (entry != null ? entry.getDestUser() : "");
	}

	public String getDestPtu(String name) {
		VoipAccount entry = entries.get(name);
		return (entry != null ? entry.getDestPTU() : null);
	}
	
	public void setDestUser(String name, String userDest) {
		entries.get(name).setDestUser(userDest);
	}
	
	public void setDestPTU(String name, String PTUDest) {
		entries.get(name).setDestPTU(PTUDest);
	}
	
	public void setDestPTUser(String name, String userDest, String PTUDest) {
		entries.get(name).setDestUser(userDest);
		entries.get(name).setDestPTU(PTUDest);
	}

	
	// Status Methods
	public Boolean getStatus(String name) {
		VoipAccount entry = entries.get(name);
		return (entry != null ? entry.getStatus() : false);
	}

	public void setStatus(String name, Boolean status) {
		entries.get(name).setStatus(status);
	}
	
	// Define all PTU as status unknown
	public void setUnknownStatus(){
		for(String name : entries.keySet())
			setStatus(name, false);
	}

	
	// On Call Status Methods
	public Boolean getOnCall(String name) {
		VoipAccount entry = entries.get(name);
		return (entry != null ? entry.getOnCall() : false);
	}

	public void setOnCall(String name, Boolean onCall) {
		entries.get(name).setOnCall(onCall);
	}

	
	// On Conference Status Methods
	public Boolean getOnConference(String name) {
		VoipAccount entry = entries.get(name);
		return (entry != null ? entry.getOnConference() : false);
	}

	public void setOnConference(String name, Boolean onConference) {
		entries.get(name).setOnConference(onConference);
	}

	
	// Room Methods
	public String getRoom(String name) {
		VoipAccount entry = entries.get(name);
		return (entry != null ? entry.getRoom() : "");
	}

	public void setRoom(String name, String conferenceRoom) {
		entries.get(name).setRoom(conferenceRoom);
	}
	
	
	// Mute Methods
	public boolean getMute(String name){
		VoipAccount entry = entries.get(name);
		return (entry != null? entry.getMute() : true);
	}
	
	public void setMute(String name, boolean mute){
		entries.get(name).setMute(mute);
	}
	
	
	// Activity Methods
	public String getActivity(String name) {
		VoipAccount entry = entries.get(name);
		return (entry != null ? entry.getActivity() : "");
	}

	public void setActivity(String name, String activity) {
		entries.get(name).setActivity(activity);
	}

	
	// PTU Ids
	public List<String> getPtuIds() {
		List<String> list = new ArrayList<String>();
		list.addAll(entries.keySet());
		return list;
	}

	
	// Add PTU
	public boolean add(String name) {
		System.err.println("Adding " + name);
		if (!entries.containsKey(name)) {
			entries.put(name, new VoipAccount("SIP/1000", false));
			return true;
		}
		return false;
	}

	
	// Check PTU is in the list
	public boolean contains(String name) {
		if (entries.containsKey(name)) {
			return true;
		}
		return false;
	}

	
	// Return PTU ID of given number
	public String getPtuId(String number) {
		List<String> ptuList = new ArrayList<String>(this.getPtuIds());
		for (int i = 0; i < ptuList.size(); i++) {
			if (this.getNumber(ptuList.get(i)).equals(number)) {
				return ptuList.get(i);
			}
		}
		return null;
	}

	// Return users phone numbers of given activity
	public List<String> getNumbersActivity(String activity) {
		List<String> ptuList = new ArrayList<String>(this.getPtuIds());
		List<String> numbers = new ArrayList<String>();
		for (int i = 0; i < ptuList.size(); i++) {
			if (this.getActivity(ptuList.get(i)).equals(activity)) {
				numbers.add(this.getNumber(ptuList.get(i)));
			}
		}
		return numbers;
	}
	
	// Return active channels of given activity
	public List<String> getActiveChannelsActivity(String activity) {
		List<String> ptuList = new ArrayList<String>(this.getPtuIds());
		List<String> channels = new ArrayList<String>();
		for (int i = 0; i < ptuList.size(); i++) {
			if (this.getActivity(ptuList.get(i)).equals(activity) && !this.getChannel(ptuList.get(i)).isEmpty()) {
				channels.add(this.getChannel(ptuList.get(i)));
			}
		}
		return channels;
	}
}
