package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

public class CO extends Measurement<Double> {

	public CO(int ptuId, double value) {
		super(ptuId, "CO Sensor", value, "ppm", new Date());
	}
	
}
