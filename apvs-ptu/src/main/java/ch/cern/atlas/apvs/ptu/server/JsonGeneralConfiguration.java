package ch.cern.atlas.apvs.ptu.server;

import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.GeneralConfiguration;

public class JsonGeneralConfiguration extends JsonMessage {
	
	String type;
	String dosimeterId;
	
	public JsonGeneralConfiguration(Message message) {
		GeneralConfiguration configuration = (GeneralConfiguration)message;
		type = configuration.getType();
		dosimeterId = configuration.getDosimeterId();
	}
}
