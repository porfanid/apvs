package ch.cern.atlas.apvs.ptu.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.cern.atlas.apvs.domain.Dosimeter;
import ch.cern.atlas.apvs.domain.Measurement;

public class PtuSocket implements Runnable {

	private Socket socket;
	private Random random = new Random();
	private int noOfDosimeters = 5;

	public PtuSocket(Socket socket) {
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
			for (int i = 0; i < noOfDosimeters; i++) {
				dosimeters.add(new Dosimeter(random.nextInt(100000), random
						.nextDouble()*500.0, random.nextDouble()*5.0));
				System.out.println(dosimeters.get(i).getSerialNo());
			}
			
			System.out.print("Connected on: "+socket.getInetAddress());

			BufferedWriter os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			while (true) {
				for (int i = 0; i < dosimeters.size(); i++) {
					Dosimeter dosimeter = dosimeters.get(i);
//					os.write(DosimeterCoder.encode(dosimeter));
					os.newLine();
					dosimeters.set(i, next(dosimeter));
				}
				os.flush();

				try {
					Thread.sleep(5000);
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
	
	public static String toXml(Measurement<?> m) {
		String i = "    ";
		String newLine = "\n";
		StringBuffer s = new StringBuffer();
		s.append(i).append("<message type=\"measurement\">").append(newLine);
		s.append(i).append(i).append("<field name=\"ptu_id\">").append(m.getPtuId()).append("</field>").append(newLine);
		s.append(i).append(i).append("<field name=\"sensor\">").append(m.getName()).append("</field>").append(newLine);
		s.append(i).append(i).append("<field name=\"value\">").append(m.getValue()).append("</field>").append(newLine);
		s.append(i).append(i).append("<field name=\"unit\">").append(m.getUnit()).append("</field>").append(newLine);
		s.append(i).append(i).append("<field name=\"datetime\">").append(PtuConstants.dateFormat.format(m.getDate())).append("</field>").append(newLine);
		s.append(i).append(i).append("<field name=\"type\">").append(m.getType()).append("</field>").append(newLine);
        s.append(i).append("</message>").append(newLine);
        return s.toString();
	}

	private Dosimeter next(Dosimeter dosimeter) {
		double newDose = dosimeter.getDose() + dosimeter.getRate();
		double newRate = Math.max(0.0,
				dosimeter.getRate() + random.nextGaussian() * 0.5);
		return new Dosimeter(dosimeter.getSerialNo(), newDose, newRate);
	}

}
