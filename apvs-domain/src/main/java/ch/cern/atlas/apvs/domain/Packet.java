package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Packet implements Serializable, IsSerializable {

	private static final long serialVersionUID = -1225311491360327143L;

	private String sender;
	private String receiver;
	private int frameId;
	private boolean acknowledge;
	
	List<Message> messages = new ArrayList<Message>();
	
	public Packet() {
	}
	
	public Packet(String sender, String receiver, int frameId, boolean acknowledge) {
		this.sender = sender;
		this.receiver = receiver;
		this.frameId = frameId;
		this.acknowledge = acknowledge;
	}
	
	public void addMessage(Message msg) {
		messages.add(msg);
	}
	
	public List<Message> getMessages() {
		return messages;
	}
		
	public String toString() {
		return "Packet "+sender+" "+receiver+" "+frameId+" "+acknowledge+" "+messages.size();
	}
	
}
