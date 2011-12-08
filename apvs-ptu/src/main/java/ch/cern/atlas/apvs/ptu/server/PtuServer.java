package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.net.ServerSocket;


public class PtuServer {

	private final static int portNo = 4005;
	private boolean json;
	private ServerSocket server;
	
	private PtuServer(boolean json) {
		this.json = json;
		
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
		System.out.println("PTU Demo Server open at "+portNo);

		while (true) {
			try {
				PtuSocket socket = new PtuSocket(server.accept(), json);
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
		boolean json = args.length > 0 ? args[0].equalsIgnoreCase("xml") ? false : true : true;
		new PtuServer(json).run();
	}
}
