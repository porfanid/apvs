package ch.cern.atlas.apvs.ptu.server;

import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.SensorOrder;

public class JsonSensorOrder extends JsonOrder {
	
	String name;
	
	public JsonSensorOrder(Message message) {
		super(message);
		SensorOrder order = (SensorOrder)message;
		name = order.getName();
	}

}
