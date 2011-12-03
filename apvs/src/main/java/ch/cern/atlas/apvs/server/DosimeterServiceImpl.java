package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.client.service.DosimeterService;
import ch.cern.atlas.apvs.domain.Dosimeter;
import ch.cern.atlas.apvs.dosimeter.server.DosimeterReader;
import ch.cern.atlas.apvs.dosimeter.server.DosimeterWriter;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterChangedEvent;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterSerialNumbersChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.server.ResponseHandler.Response;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class DosimeterServiceImpl extends ResponsePollService implements
		DosimeterService, Runnable {

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

	private ResponseHandler<List<Integer>, List<Integer>> getSerialNumbersResponseHandler = new ResponseHandler<List<Integer>, List<Integer>>(
			this);

	public List<Integer> getSerialNumbers(long currentHashCode) {
		return dosimeterReader != null ? dosimeterReader
				.getDosimeterSerialNumbers() : null;
	}

	/*
	 * @Override public List<Integer> getSerialNumbers(long currentHashCode) {
	 * return getSerialNumbersResponseHandler.respond(currentHashCode, new
	 * Response<List<Integer>, List<Integer>>() {
	 * 
	 * @Override public List<Integer> getValue(List<Integer> object) { return
	 * dosimeterReader != null ? dosimeterReader.getDosimeterSerialNumbers() :
	 * null; } }); }
	 */
	private ResponseHandler<Dosimeter, Dosimeter> getDosimeterResponseHandler = new ResponseHandler<Dosimeter, Dosimeter>(
			this);

	@Override
	public Dosimeter getDosimeter(final int serialNo, long currentHashCode) {
		return getDosimeterResponseHandler.respond(currentHashCode,
				new Response<Dosimeter, Dosimeter>() {

					@Override
					public Dosimeter getValue(Dosimeter object) {
						return dosimeterReader != null ? dosimeterReader
								.getDosimeter(serialNo) : null;
					}
				});
	}

	private ResponseHandler<Map<Integer, Dosimeter>, Map<Integer, Dosimeter>> getDosimeterMapResponseHandler = new ResponseHandler<Map<Integer, Dosimeter>, Map<Integer, Dosimeter>>(
			this);

	@Override
	public Map<Integer, Dosimeter> getDosimeterMap(long currentHashCode) {
		return getDosimeterMapResponseHandler
				.respond(
						currentHashCode,
						new Response<Map<Integer, Dosimeter>, Map<Integer, Dosimeter>>() {

							@Override
							public Map<Integer, Dosimeter> getValue(
									Map<Integer, Dosimeter> object) {
								return dosimeterReader != null ? dosimeterReader
										.getDosimeterMap() : null;
							}
						});
	}

}
