package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Message;

public class JsonMeasurement extends JsonMessage {
	
	String type;
	String sensor;
	Date time;
	String method = "OneShoot";
	String value;
	String samplingRate;
	String unit;
	
	public JsonMeasurement(Message message) {
		Measurement measurement = (Measurement)message;
		type = measurement.getType();
		sensor = limit(measurement.getName(), 50);
		time = measurement.getDate();
		value = limit(measurement.getValue().toString(), 1024);
		samplingRate = limit(measurement.getSamplingRate().toString(), 20);
		unit = measurement.getUnit();
	}
}
