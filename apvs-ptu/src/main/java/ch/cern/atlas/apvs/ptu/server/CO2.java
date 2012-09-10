package ch.cern.atlas.apvs.ptu.server;

import java.util.Date;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class CO2 extends Measurement<Double> {

	public CO2(String ptuId, double value, Date d) {
		super(ptuId, "CO<sub>2</sub> Sensor", value, "ppm", d);
	}
	
}
