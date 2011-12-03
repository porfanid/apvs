package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.dosimeter.server.DosimeterReader;
import ch.cern.atlas.apvs.dosimeter.server.DosimeterWriter;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class DosimeterServiceImpl extends ResponsePollService implements
		Runnable {

	private static final String name = "DosimeterSocket";
	private static final String host = "localhost";
	private static final int port = 4001;
	private static final int RECONNECT_INTERVAL = 20000;
	private boolean stopped = false;
	private DosimeterReader dosimeterReader;
	private RemoteEventBus eventBus;

	public DosimeterServiceImpl() {
		System.out.println("Creating DosimeterService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("Starting DosimeterService...");

		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {

		while (!stopped) {
			if (dosimeterReader == null) {
				try {
					Socket socket = new Socket(host, port);
					System.out.println("Connected to " + name + " on " + host
							+ ":" + port);

					DosimeterWriter dosimeterWriter = new DosimeterWriter(
							socket);
					Thread writer = new Thread(dosimeterWriter);
					writer.start();

					dosimeterReader = new DosimeterReader(eventBus, socket);

					Thread reader = new Thread(dosimeterReader);
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

				if (dosimeterReader != null) {
					dosimeterReader.close();
				}
				dosimeterReader = null;
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

		if (dosimeterReader != null) {
			dosimeterReader.close();
		}
	}

}
