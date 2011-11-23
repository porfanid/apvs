package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

public class Humidity extends Measurement<Double> {

	public Humidity(int ptuId, double value) {
		super(ptuId, "Humidity", value, "ppm", new Date());
	}
	
}
