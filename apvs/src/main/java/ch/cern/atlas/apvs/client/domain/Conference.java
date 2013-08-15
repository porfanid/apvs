package ch.cern.atlas.apvs.client.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Conference implements Serializable, IsSerializable {
	
	private static final long serialVersionUID = 1L;
	
	private int userNum;
	private String activity;
	private List<String> ptus;
	private List<String> usernames;
	
	public Conference(){
		userNum   = 0;
		activity  = "";
		ptus    = new ArrayList<String>();
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
	public List<String> getPtus(){
		return this.ptus;
	}
	
	public void addPtu(String ptu){
		this.ptus.add(ptu);
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

	public boolean containsPtu(String ptu) {
		for(int i = 0; i < this.ptus.size(); i++){
			if(this.ptus.get(i).equals(ptu))
				return true;
		}
		return false;
	}
	
}
