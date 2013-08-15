package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.Message;

public class JsonEvent extends JsonMessage {
	
	String type;
	String sensor;
	Date time;
	String eventType;
	String value;
	String threshold;
	
	public JsonEvent(Message message) {
		Event event = (Event)message;
		type = event.getType();
		sensor = limit(event.getSensor(), 50);
		time = event.getDate();
		value = limit(event.getValue().toString(), 1024);
		eventType = limit(event.getEventType(), 50);
		threshold = limit(event.getThreshold().toString(), 1024);
	}

}
