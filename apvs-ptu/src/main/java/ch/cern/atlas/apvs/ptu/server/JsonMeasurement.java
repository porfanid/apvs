package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Message;

public class JsonMeasurement implements JsonMessage {
	
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
		sensor = measurement.getName();
		time = measurement.getDate();
		value = measurement.getValue().toString();
		samplingRate = measurement.getSamplingRate().toString();
		unit = measurement.getUnit();
	}
}
