package ch.cern.atlas.apvs.dosimeter.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Random;

public class DosimeterSocket implements Runnable {

	private Socket socket;
	private Random random = new Random();
	
	public DosimeterSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			Dosimeter dosimeter = new Dosimeter(random.nextInt(100000), random.nextInt(500), random.nextInt(5));
			
			OutputStream os = socket.getOutputStream();
			PrintStream out = new PrintStream(os);
			while (true) {
				out.println(Dosimeter.encode(dosimeter));
				out.flush();
				dosimeter = dosimeter.next();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// ignored
				}
				System.err.print(".");
				System.err.flush();
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
