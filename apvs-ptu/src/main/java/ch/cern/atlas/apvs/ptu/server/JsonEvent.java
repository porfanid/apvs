package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.Message;

public class JsonEvent implements JsonMessage {
	
	String type;
	String sensor;
	Date time;
	String eventType;
	String value;
	
	public JsonEvent(Message message) {
		Event event = (Event)message;
		type = event.getType();
		sensor = event.getName();
		time = event.getDate();
		value = event.getValue().toString();
		eventType = event.getEventType();
	}

}
