package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class HeartBeat extends Measurement<Double> {

	public HeartBeat(String ptuId, double value, Date d) {
		super(ptuId, "Heart Rate", value, "bpm", d);
	}
	
}
