package ch.cern.atlas.apvs.ptu.server;

import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.Order;

public class JsonOrder implements JsonMessage {
	
	String type;
	String sensor;
	String parameter;
	String value;
	
	public JsonOrder(Message message) {
		Order order = (Order)message;
		type = order.getType();
		sensor = order.getName();
		parameter = order.getParameter();
		value = order.getValue().toString();
	}

}
