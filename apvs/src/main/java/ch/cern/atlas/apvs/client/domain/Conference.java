package ch.cern.atlas.apvs.client.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conference implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int userNum;
	private String activity;
	private List<String> ptuIds;
	private List<String> usernames;
	
	public Conference(){
		userNum   = 0;
		activity  = "";
		ptuIds    = new ArrayList<String>();
		usernames = new ArrayList<String>();
	}
	
	// User Number Methods
	public int getUserNum(){
		return this.userNum;
	}
	
	public void setUserNum(int userNum){
		this.userNum = userNum;
	}
	
	// Activity Methods
	public String getActivity(){
		return this.activity;
	}
	
	public void setActivity(String activity){
		this.activity = activity;
	}
	
	// PTU Methods
	public List<String> getPtuIds(){
		return this.ptuIds;
	}
	
	public void addPtu(String ptuId){
		this.ptuIds.add(ptuId);
	}
	
	// Username Methods
	public List<String> getUsernames(){
		return this.usernames;
	}
	
	public void addUsername(String username){
		this.usernames.add(username);
	}

	public boolean containsUsername(String username) {
		for(int i = 0; i < this.usernames.size(); i++){
			if(this.usernames.get(i).equals(username))
				return true;
		}
		return false;
	}

	public boolean containsPtu(String ptuId) {
		for(int i = 0; i < this.ptuIds.size(); i++){
			if(this.ptuIds.get(i).equals(ptuId))
				return true;
		}
		return false;
	}
	
}
