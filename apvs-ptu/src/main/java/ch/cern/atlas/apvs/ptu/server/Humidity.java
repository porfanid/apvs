package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class Humidity extends Measurement {

	public Humidity(String ptuId, double value, Date d) {
		super(ptuId, "Humidity", value, 50.0, 130.0, "ppm", 60000, d);
	}
	
}
