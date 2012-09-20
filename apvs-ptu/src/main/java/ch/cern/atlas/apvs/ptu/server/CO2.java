package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class CO2 extends Measurement {

	public CO2(String ptuId, double value, Date d) {
		super(ptuId, "CO2", value, "ppm", d);
	}
	
}
