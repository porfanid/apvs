package ch.cern.atlas.apvs.dosimeter.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DosimeterServer {

	private void run() throws IOException {
		ServerSocket server = new ServerSocket(4001);
		System.err.println("Server open at 4001");

		while (true) {
			try {
				DosimeterSocket socket = new DosimeterSocket(server.accept());
				Thread writer = new Thread(socket);				
				writer.start();
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
