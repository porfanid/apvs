package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

public class O2 extends Measurement<Double> {

	public O2(int ptuId, double value) {
		super(ptuId, "O<sub>2</sub> Sensor", value, "ppm", new Date());
	}
	
}
