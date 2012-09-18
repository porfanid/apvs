package ch.cern.atlas.apvs.ptu.server;

import ch.cern.atlas.apvs.domain.Measurement;

public class JsonHeader {

	transient static int currentFrameID = 0;
	
	String sender;
	String receiver = "Broadcast";
	String frameID;
	String acknowledge = "False"; 
	JsonMessage[] messages;
	
	public JsonHeader(Measurement measurement) {
		currentFrameID++;
		frameID = String.valueOf(currentFrameID);
		sender = measurement.getPtuId();
		messages = new JsonMessage[1];
		messages[0] = new JsonMessage(measurement);
	}
}
