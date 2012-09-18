package ch.cern.atlas.apvs.ptu.server;

import ch.cern.atlas.apvs.domain.Measurement;

public class JsonHeader {

	transient static int currentFrameID = 0;
	
	String sender;
	String receiver = "Broadcast";
	String frameID;
	String acknowledge = "False"; 
	JsonMessage[] messages;
	
/*
	public JsonHeader(String sender, String receiver, String frameID, String acknowledge, JsonMessage[] messages) {
		this.sender = sender;
		this.receiver = receiver;
		this.frameID = frameID;
		this.acknowledge = acknowledge;
		this.messages = messages;
	}
*/	
	public JsonHeader(Measurement<Double> measurement) {
		currentFrameID++;
		frameID = String.valueOf(currentFrameID);
		sender = measurement.getPtuId();
		messages = new JsonMessage[1];
		messages[0] = new JsonMessage(measurement);
	}
}
