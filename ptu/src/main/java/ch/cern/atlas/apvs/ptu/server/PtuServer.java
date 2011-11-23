package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.net.ServerSocket;

import com.cedarsoftware.util.io.JsonWriter;

import ch.cern.atlas.apvs.domain.Temperature;

public class PtuServer {

	private ServerSocket server;
	
	private PtuServer() {
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
		server = new ServerSocket(4005);
		System.out.println("Server open at 4005");

		while (true) {
			try {
				PtuSocket socket = new PtuSocket(server.accept());
				Thread writer = new Thread(socket);				
				writer.start();
			} catch (IOException e) {
				System.out.println("Accept failed: 4005");
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
		JsonWriter writer = new PtuJsonWriter(System.err);
		
		int ptuId = 643;
		Temperature temperature = new Temperature(ptuId, 22.9);
		System.err.println(temperature);
		writer.write(temperature);		
		System.err.println();
		System.err.println(PtuSocket.toXml(temperature));
		
//		new PtuServer().run();
	}
}
