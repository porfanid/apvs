package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class HeartBeat extends Measurement<Double> {

	public HeartBeat(int ptuId, double value) {
		super(ptuId, "Heart Rate", value, "bpm", new Date());
	}
	
}
