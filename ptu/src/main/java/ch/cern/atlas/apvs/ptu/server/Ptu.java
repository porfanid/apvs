package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ch.cern.atlas.apvs.domain.Measurement;

public class Ptu {

	private Random random = new Random();
	
	private int ptuId;
	List<Measurement<Double>> measurements = new ArrayList<Measurement<Double>>();

	public Ptu(int ptuId) {
		this.ptuId = ptuId;
		
		measurements.add(new Temperature(ptuId, 25.7 + random.nextGaussian()));
		measurements.add(new Humidity(ptuId, 31.4 + random.nextGaussian()));
		measurements.add(new CO(ptuId, 2.5 + random.nextGaussian()/10));
		measurements.add(new O2(ptuId, 85.2 + random.nextGaussian()));
	}
	
	public int getPtuId() {
		return ptuId;
	}

	public void write(ObjectWriter writer) throws IOException {
		for (Iterator<Measurement<Double>> i = measurements.iterator(); i.hasNext(); ) {
			writer.write(i.next());
			writer.newLine();
		}
	}

	public void next(ObjectWriter writer) throws IOException {
		int index = random.nextInt(measurements.size());
		Measurement<Double> m = next(measurements.get(index));
		measurements.set(index, m);
		writer.write(m);
		writer.newLine();
	}

	private Measurement<Double> next(Measurement<Double> m) {
		return new Measurement<Double>(m.getPtuId(), m.getName(), m.getValue()+random.nextGaussian(), m.getUnit(), new Date());
	}

}
