package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.domain.Measurement;

@SuppressWarnings("serial")
public class NullMeasurement extends Measurement {

	public NullMeasurement(String ptuId, String name) {
		super(ptuId, name, null, null, null);
	}
}
