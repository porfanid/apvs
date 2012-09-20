package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.domain.Report;

public class PtuSimulator extends Thread {

	private final Channel channel;
	private final Random random = new Random();
	private final int defaultWait = 5000;
	private final int extraWait = 2000;
	private final int deltaStartTime = 12 * 3600 * 1000;
	private final String ptuId;
	private Ptu ptu;

	public PtuSimulator(Channel channel, String ptuId) {
		this.channel = channel;
		this.ptuId = ptuId;
	}

	@Override
	public void run() {
		try {
			long now = new Date().getTime();
			long then = now - deltaStartTime;
			Date start = new Date(then);

			ptu = new Ptu(ptuId);

			ptu.addMeasurement(new Temperature(ptuId, 25.7, start));
			ptu.addMeasurement(new Humidity(ptuId, 31.4, start));
			ptu.addMeasurement(new CO2(ptuId, 2.5, start));
			ptu.addMeasurement(new BodyTemperature(ptuId, 37.2, start));
			ptu.addMeasurement(new HeartRate(ptuId, 120, start));
			ptu.addMeasurement(new DoseAccum(ptuId, 400.2, start));
			ptu.addMeasurement(new DoseRate(ptuId, 0.1, start));
			ptu.addMeasurement(new O2(ptuId, 85.2, start));

			System.out.println(ptuId);

			then += defaultWait + random.nextInt(extraWait);

			ChannelBuffer buffer = ChannelBuffers.buffer(8192);
			ChannelBufferOutputStream cos = new ChannelBufferOutputStream(
					buffer);

			ObjectWriter writer = new PtuJsonWriter(cos);

			int i = 0;
			
			try {
				// now loop at current time
				while (!isInterrupted()) {
					if (i % 5 == 0) {
						writer.write(nextReport(ptu, new Date()));
					} else {
						writer.write(nextMeasurement(ptu, new Date()));
					}
					writer.newLine();
					writer.flush();

					synchronized (channel) {
						channel.write(cos.buffer()).awaitUninterruptibly();
						cos.buffer().clear();
					}
					Thread.sleep(defaultWait + random.nextInt(extraWait));
					System.out.print(".");
					System.out.flush();
					i++;
				}
			} catch (InterruptedException e) {
				// ignored
			}
			System.err.print("*");
			System.out.flush();
			writer.close();
		} catch (IOException e) {
			// ignored
		} finally {
			System.out.println("Closing");
			channel.close();
		}
	}

	private Measurement nextMeasurement(Ptu ptu, Date d) {
		int index = random.nextInt(ptu.getSize());
		String name = ptu.getMeasurementNames().get(index);
		Measurement measurement = nextMeasurement(ptu.getMeasurement(name), d);
		ptu.addMeasurement(measurement);
		return measurement;
	}

	private Measurement nextMeasurement(Measurement m, Date d) {
		return new Measurement(m.getPtuId(), m.getName(), m.getValue().doubleValue()
				+ random.nextGaussian(), m.getUnit(), d);
	}
	
	private Report nextReport(Ptu ptu, Date d) {
		return new Report(ptu.getPtuId(), random.nextGaussian(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), d);
	}
}
