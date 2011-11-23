package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
				ptus.add(new Ptu(random.nextInt(300)));
				System.out.println(ptus.get(i).getPtuId());
			}
			
			System.out.print("Connected on: "+socket.getInetAddress());

			OutputStream os = socket.getOutputStream();
			ObjectWriter writer = json ? new PtuJsonWriter(os) : new PtuXmlWriter(os);
			for (int i = 0; i < ptus.size(); i++) {
				Ptu ptu = ptus.get(i);
				ptu.write(writer);
			}
			writer.flush();

			while (true) {
				ptus.get(random.nextInt(ptus.size())).next(writer);
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
}
