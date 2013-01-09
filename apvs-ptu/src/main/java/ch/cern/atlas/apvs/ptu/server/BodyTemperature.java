package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class BodyTemperature extends Measurement {

	public BodyTemperature(String ptuId, double value, Date d) {
		super(ptuId, "BodyTemperature", value, 50.0, 100.0, "&deg;C", 15000, d);
	}
	
}
