package ch.cern.atlas.apvs.dosimeter.server;

import java.io.IOException;
import java.net.ServerSocket;

public class DosimeterServer {

	private final static int portNo = 4001;
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
		server = new ServerSocket(portNo);
		System.out.println("Dosimeter Demo Server open at "+portNo);

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
		new DosimeterServer().run();
	}
}
