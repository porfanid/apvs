package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class BodyTemperature extends Measurement {

	public BodyTemperature(Device device, double value, Date d) {
		super(device, "BodyTemperature", value, 50.0, 100.0, "&deg;C", 15000, d);
	}
	
}
