package ch.cern.atlas.apvs.ptu.raw.server;

import java.io.IOException;
import java.net.ServerSocket;

public class PtuRawServer {

	private final static int portNo = 2300;
	private ServerSocket server;
	
	private PtuRawServer() {
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
		server = new ServerSocket(portNo);
		System.out.println("PTU Raw Demo Server open at "+portNo);

		while (true) {
			try {
				DosimeterSocket socket = new DosimeterSocket(server.accept());
				Thread writer = new Thread(socket);				
				writer.start();
			} catch (IOException e) {
				System.out.println("Accept failed: "+portNo);
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
		new PtuRawServer().run();
	}
}
