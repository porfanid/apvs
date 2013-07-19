package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class DoseAccum extends Measurement {

	public DoseAccum(Device device, double value, Date d) {
		super(device, "DoseAccum", value, 50.0, 100.0, "mSv", 10000, d);
	}
}
