package ch.cern.atlas.apvs.dosimeter.server;

import java.io.IOException;
import java.net.ServerSocket;

public class DosimeterServer {

	private void run() throws IOException {
		ServerSocket server = new ServerSocket(4001);
		System.err.println("Server open at 4001");

		while (true) {
			DosimeterSocket socket;
			try {
				socket = new DosimeterSocket(server.accept());
				Thread thread = new Thread(socket);
				thread.start();
			} catch (IOException e) {
				System.out.println("Accept failed: 4001");
				System.exit(-1);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new DosimeterServer().run();
	}

}
