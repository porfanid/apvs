package ch.cern.atlas.apvs.ptu.daq.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;

public class PtuDaqServer {

	private final static int portNo = 4005;

	private ServerSocket server;
	private PtuDaqQueue queue;

	private PtuDaqServer() throws IOException {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					finalize();
				} catch (Throwable e) {
					// ignored
				}
			}
		});

		queue = new PtuDaqQueue();
		queue.start();

		// start all the readers
		BufferedReader in = new BufferedReader(new FileReader("PtuDaq.conf"));
		String line;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("#")) continue;
			
			String[] s = line.split(":", 2);

			if (s.length > 1) {
				new PtuDaqReader(s[0], Integer.parseInt(s[1]), queue).start();
			}
		}
		in.close();
	}

	private void run() throws IOException {
		server = new ServerSocket(portNo);
		System.out.println("PTU DAQ Server open at " + portNo);

		while (true) {
			try {
				new PtuDaqSocket(server.accept(), queue);
			} catch (IOException e) {
				System.out.println("Accept failed: " + portNo);
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
		new PtuDaqServer().run();
	}
}