package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

public class Message {
	
	String type;
	String sensor;
	Date time;
	String method = "OneShoot";
	String value;
	String samplerate = "";
	String unit;
	
	public Message(Measurement<Double> measurement) {
		type = measurement.getType();
		sensor = measurement.getName();
		time = measurement.getDate();
		value = Double.toString(measurement.getValue());
		unit = measurement.getUnit();
	}
}
