package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;

public class PtuSocket implements Runnable {

	private Socket socket;
	private boolean json;
	private Random random = new Random();
	private int noOfPtus = 6;
	private int defaultWait = 5000;
	private int extraWait = 2000;
	private int deltaStartTime = 12 * 3600 * 1000;

	public PtuSocket(Socket socket, boolean json) {
		this.socket = socket;
		this.json = json;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (socket != null) {
			System.out.println("Closing socket");
			socket.close();
		}
	}

	@Override
	public void run() {
		try {
			long now = new Date().getTime();
			long then = now - deltaStartTime;
			Date start = new Date(then);

			int[] ptuIds = { 78347, 82098, 37309, 27372, 39400, 88982 };
			List<Ptu> ptus = new ArrayList<Ptu>(noOfPtus);
			for (int i = 0; i < noOfPtus; i++) {
				int ptuId = ptuIds[i];
				Ptu ptu = new Ptu(ptuId);
				ptus.add(ptu);
					
				ptu.addMeasurement(new Temperature(ptuId, 25.7, start));
				ptu.addMeasurement(new Humidity(ptuId, 31.4, start));
				ptu.addMeasurement(new CO2(ptuId, 2.5, start));
				ptu.addMeasurement(new BodyTemperature(ptuId, 37.2, start));
				ptu.addMeasurement(new HeartBeat(ptuId, 120, start));
				ptu.addMeasurement(new O2SkinSaturationRate(ptuId, 20.8, start));
				ptu.addMeasurement(new O2(ptuId, 85.2, start));

				System.out.println(ptus.get(i).getPtuId());
			}

			then += defaultWait + random.nextInt(extraWait);

			System.out.print("PTU Demo Server connected on: "
					+ socket.getInetAddress());

			OutputStream os = socket.getOutputStream();
			ObjectWriter writer = json ? new PtuJsonWriter(os)
					: new PtuXmlWriter(os);

			// loop in the past
			while (then < now) {
				writer.write(next(ptus.get(random.nextInt(ptus.size())), new Date(then)));
				writer.newLine();
				then += defaultWait + random.nextInt(extraWait);
			}
			writer.flush();

			// now loop at current time
			while (true) {
				writer.write(next(ptus.get(random.nextInt(ptus.size())),
						new Date()));
				writer.newLine();

				writer.flush();

				try {
					Thread.sleep(defaultWait + random.nextInt(extraWait));
				} catch (InterruptedException e) {
					// ignored
				}
				System.out.print(".");
				System.out.flush();
			}

		} catch (IOException e) {
		} finally {
			try {
				System.out.println("Closing");
				socket.close();
			} catch (IOException e) {
				// ignored
			}
		}
	}

	private Measurement<Double> next(Ptu ptu, Date d) {
		int index = random.nextInt(ptu.getSize());
		String name = ptu.getMeasurementNames().get(index);
		Measurement<Double> measurement = next(ptu.getMeasurement(name), d);
		ptu.addMeasurement(measurement);
		return measurement;
	}

	private Measurement<Double> next(Measurement<Double> m, Date d) {
		return new Measurement<Double>(m.getPtuId(), m.getName(), m.getValue()
				+ random.nextGaussian(), m.getUnit(), d);
	}
}
