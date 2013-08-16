package ch.cern.atlas.apvs.ptu.server;

import java.util.ArrayList;
import java.util.List;

import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Message;

public class JsonHeader {

	transient static int currentFrameID = 0;
	
	String sender;
	String receiver;
	int frameID;
	boolean acknowledge;
	List<JsonMessage> messages;
	
	// sending only
	public JsonHeader(Message message) {
		this.sender = message.getDevice().getName();
		this.receiver = "Broadcast";
		currentFrameID++;
		frameID = currentFrameID;
		this.messages = new ArrayList<JsonMessage>();
		addMessage(new JsonMessage(message));
	}

	public JsonHeader(String sender, String receiver, int frameID,
			boolean acknowledge) {
		this.sender = sender;
		this.receiver = receiver;
		this.frameID = frameID;
		this.acknowledge = acknowledge;
		this.messages = new ArrayList<JsonMessage>();
	}
	
	public void addMessage(JsonMessage message) {
		messages.add(message);
	}

	public List<Message> getMessages(Device device) {
		List<Message> result = new ArrayList<Message>(messages.size());
		for (JsonMessage m : messages) {
			result.add(m.toMessage(device));
		}
 		return result;
	}

	public String getSender() {
		return sender;
	}
}
