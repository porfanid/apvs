package ch.cern.atlas.apvs.dosimeter.server;

import java.io.IOException;
import java.net.ServerSocket;

public class DosimeterServer {

	private ServerSocket server;
	
	private DosimeterServer() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() { 
				try {
					finalize();
				} catch (Throwable e) {
					// ignored
				}
			}
		});
	}
	
	private void run() throws IOException {
		server = new ServerSocket(4001);
		System.out.println("Server open at 4001");

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
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("Closing server");
		if (server != null) {
			System.out.println("Closing server");
			server.close();
		}
	}

	public static void main(String[] args) throws IOException {
		new DosimeterServer().run();
	}
}
