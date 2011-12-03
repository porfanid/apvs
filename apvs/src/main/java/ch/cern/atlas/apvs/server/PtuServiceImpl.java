package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.server.PtuReader;
import ch.cern.atlas.apvs.ptu.server.PtuWriter;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class PtuServiceImpl extends ResponsePollService implements Runnable {

	private static final String name = "PtuSocket";
	private static final String host = "localhost";
	private static final int port = 4005;
	private static final int RECONNECT_INTERVAL = 20000;
	private boolean stopped = false;
	private PtuReader ptuReader;

	private RemoteEventBus eventBus;

	public PtuServiceImpl() {
		System.out.println("Creating PtuService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		System.out.println("Starting PtuService...");

		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {

		while (!stopped) {
			if (ptuReader == null) {
				try {
					Socket socket = new Socket(host, port);
					System.out.println("Connected to " + name + " on " + host
							+ ":" + port);

					PtuWriter ptuWriter = new PtuWriter(socket);
					Thread writer = new Thread(ptuWriter);
					writer.start();

					ptuReader = new PtuReader(eventBus, socket);

					Thread reader = new Thread(ptuReader);
					reader.start();
					reader.join();
				} catch (UnknownHostException e) {
					System.err.println(getClass() + " " + e);
				} catch (ConnectException e) {
					System.err.println("Could not connect to " + name + " on "
							+ host + ":" + port);
				} catch (IOException e) {
					System.err.println(getClass() + " " + e);
				} catch (InterruptedException e) {
					System.err.println(getClass() + " " + e);
				}

				if (ptuReader != null) {
					ptuReader.close();
				}
				ptuReader = null;
			}

			System.err.println("Sleep");
			try {
				Thread.sleep(RECONNECT_INTERVAL);
			} catch (InterruptedException e) {
				// ignored
			}
		}
	}

	@Override
	public void destroy() {
		super.destroy();

		stopped = true;

		if (ptuReader != null) {
			ptuReader.close();
		}
	}
}
