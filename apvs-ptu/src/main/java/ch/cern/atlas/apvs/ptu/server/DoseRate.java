package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class DoseRate extends Measurement {

	public DoseRate(String ptuId, double value, Date d) {
		super(ptuId, "DoseRate", value, 50.0, 100.0, "mSv/h", 50000, d);
	}
	
}
