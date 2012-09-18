package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class Accelerometer extends Measurement {

	public Accelerometer(String ptuId, Number value) {
		super(ptuId, "Accelerometer", value, "mg", new Date());
	}
}
