package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.client.PtuService;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.ptu.server.PtuReader;
import ch.cern.atlas.apvs.ptu.server.PtuWriter;
import ch.cern.atlas.apvs.server.ResponseHandler.Response;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class PtuServiceImpl extends ResponsePollService implements PtuService,
		Runnable {

	private static final String name = "PtuSocket";
	private static final String host = "localhost";
	private static final int port = 4005;
	private static final int RECONNECT_INTERVAL = 20000;
	private boolean stopped = false;
	private PtuReader ptuReader;

	public PtuServiceImpl() {
		System.out.println("Creating PtuService...");
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
					System.err.println("Connected to " + name + " on " + host
							+ ":" + port);

					PtuWriter ptuWriter = new PtuWriter(socket);
					Thread writer = new Thread(ptuWriter);
					writer.start();

					ptuReader = new PtuReader(socket);
					ptuReader
							.addValueChangeHandler(getMeasurementResponseHandler);
					Thread reader = new Thread(ptuReader);
					reader.start();
					reader.join();
					ptuReader = null;
					continue;
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

		try {
			if (ptuReader != null) {
				ptuReader.close();
			}
		} catch (IOException e) {
			// ignored
		}
	}

	private ResponseHandler<Measurement<?>, Measurement<Double>> getMeasurementResponseHandler = new ResponseHandler<Measurement<?>, Measurement<Double>>(
			this);

	@Override
	public Measurement<Double> getMeasurement(final int ptuId, final String name,
			int currentHashCode) {
		return getMeasurementResponseHandler.respond(currentHashCode, new Response<Measurement<Double>>() {

			@Override
			public Measurement<Double> getValue() {
				return ptuReader.getPtu(ptuId).getMeasurement(name);
			}
			
		});
	}
	/*
	 * @Override public List<Integer> getSerialNumbers(int currentHashCode) {
	 * return getSerialNumbersResponseHandler.respond(currentHashCode, new
	 * Response<List<Integer>>() {
	 * 
	 * @Override public List<Integer> getValue() { return
	 * dosimeterReader.getDosimeterSerialNumbers(); } }); }
	 * 
	 * private ResponseHandler<Map<Integer, Dosimeter>, Dosimeter>
	 * getDosimeterResponseHandler = new ResponseHandler<Map<Integer,
	 * Dosimeter>, Dosimeter>( this);
	 * 
	 * @Override public Dosimeter getDosimeter(final int serialNo, int
	 * currentHashCode) { return
	 * getDosimeterResponseHandler.respond(currentHashCode, new
	 * Response<Dosimeter>() {
	 * 
	 * @Override public Dosimeter getValue() { return
	 * dosimeterReader.getDosimeter(serialNo); } }); }
	 * 
	 * private ResponseHandler<Map<Integer, Dosimeter>, Map<Integer, Dosimeter>>
	 * getDosimeterMapResponseHandler = new ResponseHandler<Map<Integer,
	 * Dosimeter>, Map<Integer, Dosimeter>>( this);
	 * 
	 * @Override public Map<Integer, Dosimeter> getDosimeterMap(int
	 * currentHashCode) { return
	 * getDosimeterMapResponseHandler.respond(currentHashCode, new
	 * Response<Map<Integer, Dosimeter>>() {
	 * 
	 * @Override public Map<Integer, Dosimeter> getValue() { return
	 * dosimeterReader.getDosimeterMap(); } }); }
	 */
}
