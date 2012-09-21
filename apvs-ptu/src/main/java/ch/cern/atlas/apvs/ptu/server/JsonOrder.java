package ch.cern.atlas.apvs.ptu.server;

import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.Order;

public class JsonOrder extends JsonMessage {
	
	String type;
	String sensor;
	String parameter;
	String value;
	
	public JsonOrder(Message message) {
		Order order = (Order)message;
		type = order.getType();
		sensor = limit(order.getName(), 50);
		parameter = order.getParameter(); // FIXME limit
		value = limit(order.getValue().toString(), 1024);
	}

}
