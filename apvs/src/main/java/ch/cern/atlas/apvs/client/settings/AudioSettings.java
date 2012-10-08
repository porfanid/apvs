package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ch.cern.atlas.apvs.client.settings.PtuSettings.Entry;


public class AudioSettings implements Serializable{

	private Map<String, Entry> entries = new HashMap<String, Entry>();
	
	public static class Entry implements Serializable{
		String username;
		String number;
		String channel;
		String destUser;
		String status;
		Boolean onCall;
		
		public Entry() {
			username = "";
			number = "";
			channel = "";
			destUser = "";
			status = "";
			onCall = false;
		}
		
		public String toString(){
			return ("VoipAccount: username=" + username +"" +
					" number= "+ number + " channel=" + channel +
					" destUser= " + destUser + " status=" + status +
					" onCall=" + onCall);
		}
	} 
	
	public AudioSettings(){		
	}
	
	
	//Methods
	public String getUsername(String ptuId){
		Entry entry = entries.get(ptuId);
		return (entry!=null ? entry.username : "");
	}

	public void setUsername(String ptuId, String username){
		entries.get(ptuId).username = username;
	}
	
	public String getNumber(String ptuId){
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.number : "");
	}
	
	public void setNumber(String ptuId, String number){
		entries.get(ptuId).number = number;
	}
	
	public String getChannel(String ptuId){
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.channel : "");
	}
	
	public void setChannel(String ptuId, String channel){
		entries.get(ptuId).channel = channel;
	}
	
	public String getDestUser(String ptuId){
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.destUser : "");
	}

	public void setDestUser(String ptuId, String user){
		entries.get(ptuId).destUser = user;
	}
	
	public String getStatus(String ptuId){
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.status : "");
	}
	
	public void setStatus(String ptuId, String status){
		entries.get(ptuId).status = status;
	}
	
	public Boolean getOnCall(String ptuId){
		Entry entry = entries.get(ptuId);
		return (entry != null ? entry.onCall : false);
	}
	
	public void setOnCall(String ptuId, Boolean onCall){
		entries.get(ptuId).onCall = onCall;
	}
	
	public boolean add(String ptuId) {
		System.err.println("Adding " + ptuId);
		if (!entries.containsKey(ptuId)) {
			entries.put(ptuId, new Entry());
			return true;
		}
		return false;
	}
	
}
