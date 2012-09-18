package ch.cern.atlas.apvs.ptu.server;

import java.text.ParseException;
import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

public class JsonMessage {
	
	String type;
	String sensor;
	Date time;
	String method = "OneShoot";
	String value;
	String samplerate = "";
	String unit;
	
	public JsonMessage(Measurement measurement) {
		type = measurement.getType();
		sensor = measurement.getName();
		time = measurement.getDate();
		value = measurement.getValue().toString();
		unit = measurement.getUnit();
	}

	public JsonMessage(String type, String sensor, String time,
			String method, String value, String samplerate, String unit) throws ParseException {
		this.type = type;
		this.sensor = sensor;
		this.time = PtuConstants.dateFormat.parse(time);
		this.method = method;
		this.value = value;
		this.samplerate = samplerate;
		this.unit = unit;
	}
}
