package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class O2SkinSaturationRate extends Measurement {

	public O2SkinSaturationRate(String ptuId, double value, Date d) {
		super(ptuId, "O<sub>2</sub> Skin Saturation Rate", value, "ppm", d);
	}
	
}
