package ch.cern.atlas.apvs.domain;

import java.util.Date;

public class Temperature extends Measurement {

	public Temperature(int ptuId, double temperature) {
		super(ptuId, "Temperature", temperature, "&deg;C", new Date());
	}
	
}
