package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.client.PtuService;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.ptu.server.PtuReader;
import ch.cern.atlas.apvs.ptu.server.PtuWriter;
import ch.cern.atlas.apvs.server.ResponseHandler.Response;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

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
		EventBus eventBus = new SimpleEventBus();
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
					ptuReader.addValueChangeHandler(getPtuIdsResponseHandler);
					ptuReader.addValueChangeHandler(getPtuResponseHandler);
					ptuReader
							.addValueChangeHandler(getLastMeasurementResponseHandler);
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

	private ResponseHandler<Measurement<?>, Ptu> getPtuResponseHandler = new ResponseHandler<Measurement<?>, Ptu>(
			this);

	@Override
	public Ptu getPtu(final int ptuId, long currentHashCode) {
		return getPtuResponseHandler.respond(currentHashCode,
				new Response<Measurement<?>, Ptu>() {

					@Override
					public Ptu getValue(Measurement<?> object) {
						return ptuReader != null ? ptuReader.getPtu(ptuId) : null;
					}

				});
	}

	private ResponseHandler<Measurement<?>, List<Integer>> getPtuIdsResponseHandler = new ResponseHandler<Measurement<?>, List<Integer>>(
			this);

	@Override
	public List<Integer> getPtuIds(long currentHashCode) {
		return getPtuIdsResponseHandler.respond(currentHashCode,
				new Response<Measurement<?>, List<Integer>>() {

					@Override
					public List<Integer> getValue(Measurement<?> object) {
						return ptuReader != null ? new ArrayList<Integer>(
								ptuReader.getPtuIds()) : null;
					}

				});
	}

	private ResponseHandler<Measurement<?>, Measurement<Double>> getMeasurementResponseHandler = new ResponseHandler<Measurement<?>, Measurement<Double>>(
			this);

	@Override
	public Measurement<Double> getMeasurement(final int ptuId,
			final String name, long currentHashCode) {
		return getMeasurementResponseHandler.respond(currentHashCode,
				new Response<Measurement<?>, Measurement<Double>>() {

					@Override
					public Measurement<Double> getValue(Measurement<?> object) {
						return ptuReader != null ? ptuReader.getPtu(ptuId)
								.getMeasurement(name) : null;
					}

				});
	}

	private ResponseHandler<Measurement<?>, Measurement<Double>> getLastMeasurementResponseHandler = new ResponseHandler<Measurement<?>, Measurement<Double>>(
			this);

	@Override
	public Measurement<Double> getLastMeasurement(long currentHashCode) {
		return getLastMeasurementResponseHandler.respond(currentHashCode,
				new Response<Measurement<?>, Measurement<Double>>() {

					@Override
					public Measurement<Double> getValue(Measurement<?> object) {
						return (Measurement<Double>) object;
					}
				});
	}

	@Override
	public List<Measurement<Double>> getCurrentMeasurements() {
		return ptuReader.getMeasurements();
	}
}
