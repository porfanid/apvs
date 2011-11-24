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
	private int noOfPtus = 3;

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
			List<Ptu> ptus = new ArrayList<Ptu>(
					noOfPtus);
			for (int i = 0; i < noOfPtus; i++) {
				int ptuId = random.nextInt(300);
				Ptu ptu = new Ptu(ptuId);
				ptus.add(ptu);
				ptu.add(new Temperature(ptuId, 25.7 + random.nextGaussian()));
				ptu.add(new Humidity(ptuId, 31.4 + random.nextGaussian()));
				ptu.add(new CO2(ptuId, 2.5 + random.nextGaussian()/10));
				ptu.add(new O2(ptuId, 85.2 + random.nextGaussian()));
				
				System.out.println(ptus.get(i).getPtuId());
			}
			
			System.out.print("Connected on: "+socket.getInetAddress());

			OutputStream os = socket.getOutputStream();
			ObjectWriter writer = json ? new PtuJsonWriter(os) : new PtuXmlWriter(os);
			for (int i = 0; i < ptus.size(); i++) {
				Ptu ptu = ptus.get(i);
				writer.write(ptu);
			}
			writer.flush();

			while (true) {
				next(ptus.get(random.nextInt(ptus.size())),writer);
				writer.flush();

				try {
					Thread.sleep(2000 + random.nextInt(2000));
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
	
	private void next(Ptu ptu, ObjectWriter writer) throws IOException {
		int index = random.nextInt(ptu.getSize());
		String name = ptu.getMeasurementNames().get(index);
		Measurement<Double> measurement = next(ptu.getMeasurement(name));
		ptu.setMeasurement(name, measurement);
		writer.write(measurement);
		writer.newLine();
	}

	private Measurement<Double> next(Measurement<Double> m) {
		return new Measurement<Double>(m.getPtuId(), m.getName(), m.getValue()+random.nextGaussian(), m.getUnit(), new Date());
	}
}
