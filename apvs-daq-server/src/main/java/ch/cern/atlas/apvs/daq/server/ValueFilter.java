package ch.cern.atlas.apvs.daq.server;

import java.util.List;

import ch.cern.atlas.apvs.domain.Measurement;

public class ValueFilter implements Filter {

	@Override
	public boolean filter(Measurement current, List<Measurement> list,
			double resolution) {
		// TODO Auto-generated method stub
		return false;
	}

}
