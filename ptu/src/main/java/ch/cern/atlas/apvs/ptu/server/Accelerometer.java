package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

public class Accelerometer extends Measurement<Double[]> {

	public Accelerometer(int ptuId, Double[] value) {
		super(ptuId, "Accelerometer", value, "mg", new Date());
	}
}
