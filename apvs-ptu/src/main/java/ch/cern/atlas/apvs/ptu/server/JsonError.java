package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Error;
import ch.cern.atlas.apvs.domain.Message;

public class JsonError implements JsonMessage {
	
	String type;
	String errorNo;
	Date time;
	String description;
	String criticality;
	
	public JsonError(Message message) {
		Error error = (Error)message;
		type = error.getType();
		errorNo = Integer.toString(error.getErrorNo());
		time = error.getDate();
		description = error.getDescription();
		criticality = error.getCriticality();
	}

}
