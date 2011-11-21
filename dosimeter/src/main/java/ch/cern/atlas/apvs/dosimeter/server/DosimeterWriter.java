package ch.cern.atlas.apvs.dosimeter.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class DosimeterWriter implements Runnable {

	private final static int PING_INTERVAL = 5000;
	private Socket socket;

	public DosimeterWriter(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		BufferedWriter os;
		try {
			os = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			while (true) {
				os.write("ping");
				os.newLine();
				os.flush();

				try {
					Thread.sleep(PING_INTERVAL);
				} catch (InterruptedException e) {
					// ignored
				}

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
