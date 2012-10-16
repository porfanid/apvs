package ch.cern.atlas.apvs.server;

public class VoipAccount {

	private String number;
	private String channel;
	private String destUser;
	private String type;
	private String status;
	private Boolean onCall;
	

	// Number	
	public String getNumber(){
		return this.number;
	}
		
	public void setNumber(String number){
		this.number = number;
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
	
	//Type
	public String getType(){
		return this.type;
	}	
	
	public void setType(String type){
		this.type = type;
	}
	
	//Status
	public String getStatus(){
		return this.status;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	//On call
	public Boolean getOnCall(){
		return this.onCall;
	}
	
	public void setOnCall(Boolean onCall){
		this.onCall = onCall;
	}
	
}
