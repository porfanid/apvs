package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class Temperature extends Measurement<Double> {

	public Temperature(String ptuId, double value, Date d) {
		super(ptuId, "Temperature", value, "&deg;C", d);
	}
	
}
