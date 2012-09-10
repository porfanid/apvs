package ch.cern.atlas.apvs.ptu.server;

import ch.cern.atlas.apvs.domain.Measurement;

public class Header {

	String sender;
	String receiver = "Broadcast";
	Integer frameID = 0;
	boolean acknowledge = false; 
	Message[] messages;
	
	public Header(Measurement<Double> measurement) {
		sender = measurement.getPtuId();
		messages = new Message[1];
		messages[0] = new Message(measurement);
	}
}
