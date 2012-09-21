package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Error;
import ch.cern.atlas.apvs.domain.Message;

public class JsonError extends JsonMessage {
	
	String type;
	String errorNo;
	Date time;
	String description;
	String criticality;
	
	public JsonError(Message message) {
		Error error = (Error)message;
		type = error.getType();
		errorNo = limit(error.getErrorNo().toString(), 20);
		time = error.getDate();
		description = limit(error.getDescription(), 500);
		criticality = limit(error.getCriticality(), 20);
	}

}
