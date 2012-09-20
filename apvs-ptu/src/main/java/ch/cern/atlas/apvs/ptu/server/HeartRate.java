package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class HeartRate extends Measurement {

	public HeartRate(String ptuId, double value, Date d) {
		super(ptuId, "HeartRate", value, "bpm", d);
	}
	
}
