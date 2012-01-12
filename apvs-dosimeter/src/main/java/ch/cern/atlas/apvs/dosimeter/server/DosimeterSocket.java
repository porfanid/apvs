package ch.cern.atlas.apvs.dosimeter.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ch.cern.atlas.apvs.domain.Dosimeter;

public class DosimeterSocket implements Runnable {

	private Socket socket;
	private Random random = new Random();
	private int noOfDosimeters = 5;

	public DosimeterSocket(Socket socket) {
		this.socket = socket;
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
			List<Dosimeter> dosimeters = new ArrayList<Dosimeter>(
					noOfDosimeters);
			int[] serialNo = { 265, 4738, 202, 106, 395 };
			Date now = new Date();
			for (int i = 0; i < noOfDosimeters; i++) {
				dosimeters.add(new Dosimeter(serialNo[i], random
						.nextDouble()*500.0, random.nextDouble()*5.0, now));
				System.out.println(dosimeters.get(i).getSerialNo());
			}
			
			System.out.print("Dosimeter Demo Server connected on: "+socket.getInetAddress());
			
			devNull(socket.getInputStream());

			BufferedWriter os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			while (true) {
				for (int i = 0; i < dosimeters.size(); i++) {
					Dosimeter dosimeter = dosimeters.get(i);
					os.write(DosimeterCoder.encode(dosimeter));
					os.newLine();
					dosimeters.set(i, next(dosimeter));
				}
				os.flush();

				try {
					Thread.sleep(10000 + random.nextInt(5000));
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

	private Dosimeter next(Dosimeter dosimeter) {
		double newDose = dosimeter.getDose() + dosimeter.getRate();
		double newRate = Math.max(0.0,
				dosimeter.getRate() + random.nextGaussian() * 0.5);
		return new Dosimeter(dosimeter.getSerialNo(), newDose, newRate, new Date());
	}

	private void devNull(final InputStream is) {
		new Thread() {
			@Override
			public void run() {
				try {
					while (is.read() > 0) {
						// keep going
					}
				} catch (IOException e) {
					// ignored
				}
			}
		}.start();
	}

}
