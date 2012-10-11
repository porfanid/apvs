package ch.cern.atlas.apvs.server;

public class VoipAccount {

	private String number;
	private String type;
	private String status;
	
// Set Methods
	
	public void setNumber(String number){
		this.number = number;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
// Get Methods	
	public String getNumber(){
		return this.number;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getStatus(){
		return this.status;
	}
	
}
