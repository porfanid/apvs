package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class O2 extends Measurement {

	public O2(String ptuId, double value, Date d) {
		super(ptuId, "O2", value, 50.0, 100.0, "ppm", 55000, d);
	}
	
}
