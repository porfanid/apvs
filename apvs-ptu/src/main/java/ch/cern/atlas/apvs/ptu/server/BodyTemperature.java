package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class BodyTemperature extends Measurement<Double> {

	public BodyTemperature(int ptuId, double value) {
		super(ptuId, "Body Temperature", value, "&deg;C", new Date());
	}
	
}
