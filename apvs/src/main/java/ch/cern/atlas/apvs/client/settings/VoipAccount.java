package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;

public class VoipAccount implements Serializable {

	private static final long serialVersionUID = 1L;
	private String username;
	private String account;
	private String channel;
	private String destUser;
	private String destPTU;
	private Boolean status = false;
	private Boolean onCall;
	private String activity;
	private String room;
	private Boolean mute;
	private Boolean onConference;
	
	public VoipAccount() {
		this(false);
	}
	
	public VoipAccount(String account, Boolean status){
		this.username = "";
		this.account = account;
		this.channel = "";
		this.destUser = "";
		this.destPTU = "";
		this.status = status;
		this.onCall = false;
		this.activity = "";
		this.room = "";
		this.mute = false;
		this.onConference = false;
	}
	
	public VoipAccount(String account) {
		this.username = "";
		this.account = account;
		this.channel = "";
		this.destUser = "";
		this.destPTU = "";
		this.status = false;
		this.onCall = false;
		this.activity = "";
		this.room = "";
		this.mute = false;
		this.onConference = false;
	}
	
	public VoipAccount(boolean setDefaults) {
		if(!setDefaults)
			return;
		this.username = "";
		this.account = "SIP/2001";
		this.channel = "";
		this.destUser = "";
		this.destPTU = "";
		this.status = false;
		this.onCall = false;
		this.activity = "";
		this.room = "";
		this.mute = false;
		this.onConference = false;
	}

	// Username	
	public String getUsername(){
		return this.username;
	}
		
	public void setUsername(String username){
		this.username = username;
	}
	
	// Number	
	public String getAccount(){
		return this.account;
	}
		
	public void setAccount(String account){
		this.account = account;
	}

	//Parse Number (eg. SIP/1000->1000)
	public String getNumber(){
		if(isSIPValid())
			return (this.account.substring(4));
		
		return null;
	}
	
	public boolean isSIPValid(){
		if(this.account == null)
			return false;
		
		if(this.account.contains("SIP/")){
			return true;
		}
		return false;
	}
	
	//Channel
	public String getChannel(){
		return this.channel;
	}
	
	public void setChannel(String channel){
		this.channel = channel;
	}
	
	//Destination User
	public String getDestUser(){
		return this.destUser;
	}
	
	public void setDestUser(String destUser){
		this.destUser = destUser;
	}
	
	//Destination PTU
	public String getDestPTU(){
		return this.destPTU;
	}
	
	public void setDestPTU(String destPTU){
		this.destPTU = destPTU;
	}
	
	//Status
	public Boolean getStatus(){
		return this.status;
	}
	
	public void setStatus(Boolean status){
		this.status = status;
	}
	
	//On call
	public Boolean getOnCall(){
		return this.onCall;
	}
	
	public void setOnCall(Boolean onCall){
		this.onCall = onCall;
	}

	//Room
	public String getRoom(){
		return this.room;
	}
	
	public void setRoom(String room){
		this.room = room;
	}
	
	//Activity
	public String getActivity(){
		return this.activity;
	}
	
	public void setActivity(String activity){
		this.activity = activity;
	}
	
	//Mute
	public Boolean getMute(){
		return this.mute;
	}
	
	public void setMute(Boolean mute){
		this.mute = mute;
	}
	
	//On conference
	public Boolean getOnConference(){
		return this.onConference;
	}
	
	public void setOnConference(Boolean onConference){
		this.onConference = onConference;
	}
	
	public String toString() {
		return ("VoipAccount: username=" + username + " account= " + account
				+ " channel=" + channel + " destUser= " + destUser
				+ " destPTU=" + destPTU + " status=" + status 
				+ " activity=" + activity + " onCall=" + onCall 
				+ " room=" + room + " onConference="+ onConference
				+ " mute=" + mute);
	}
	
}
