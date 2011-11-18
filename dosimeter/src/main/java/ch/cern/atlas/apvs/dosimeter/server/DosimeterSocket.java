package ch.cern.atlas.apvs.dosimeter.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
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
	public void run() {
		try {
			List<Dosimeter> dosimeters = new ArrayList<Dosimeter>(noOfDosimeters);
			for (int i = 0; i<noOfDosimeters; i++) {
				dosimeters.add(new Dosimeter(random.nextInt(100000), random.nextInt(500), random.nextInt(5)));
				System.err.println(dosimeters.get(i).getSerialNo());
			}
			
			OutputStream os = socket.getOutputStream();
			PrintStream out = new PrintStream(os);
			while (true) {
				for (int i = 0; i<dosimeters.size(); i++) {
					Dosimeter dosimeter = dosimeters.get(i);
					out.println(DosimeterCoder.encode(dosimeter));
					dosimeters.set(i, next(dosimeter));
				}
				out.flush();
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// ignored
				}
				System.err.print(".");
				System.err.flush();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
				socket.close();
				} catch (IOException e) {
					// ignored
				}
			}
		}
	}
	
	private Dosimeter next(Dosimeter dosimeter) {
		double newDose = dosimeter.getDose() + dosimeter.getRate();
		double newRate = Math.max(0, dosimeter.getRate() + random.nextGaussian() * 0.5);
		return new Dosimeter(dosimeter.getSerialNo(), newDose, newRate);
	}	

}
