package ch.cern.atlas.apvs.dosimeter.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import ch.cern.atlas.apvs.domain.Dosimeter;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterChangedEvent;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterPtuChangedEvent;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterSerialNumbersChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;

import com.cedarsoftware.util.io.JsonWriter;

public class DosimeterReader implements Runnable {
	
	private RemoteEventBus eventBus;
	private Socket socket;
	private Map<Integer, Dosimeter> dosimeters;

	private Map<Integer, Integer> dosimeterToPtu;
	
	private FileHandler logHandler;

	@SuppressWarnings("serial")
	public DosimeterReader(final RemoteEventBus eventBus, Socket socket) {
		this.eventBus = eventBus;
		this.socket = socket;
		init();

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();

				if (type.equals(DosimeterSerialNumbersChangedEvent.class
						.getName())) {
					eventBus.fireEvent(new DosimeterSerialNumbersChangedEvent(
							getDosimeterSerialNumbers()));
				} else if (type.equals(DosimeterChangedEvent.class.getName())) {
					for (Iterator<Integer> i = dosimeters.keySet().iterator(); i
							.hasNext();) {
						eventBus.fireEvent(new DosimeterChangedEvent(
								getDosimeter(i.next())));
					}
				} else if (type.equals(MeasurementChangedEvent.class.getName())) {
					for (Iterator<Integer> i = dosimeters.keySet().iterator(); i
							.hasNext();) {
						Dosimeter dosimeter = getDosimeter(i.next());
						Integer ptuId = dosimeterToPtu.get(dosimeter.getSerialNo());
						if (ptuId != null) {
							sendMeasurements(ptuId, dosimeter);
						}
					}
				}
			}
		});
		
		DosimeterPtuChangedEvent.subscribe(eventBus, new DosimeterPtuChangedEvent.Handler() {
			
			@Override
			public void onDosimeterPtuChanged(DosimeterPtuChangedEvent event) {
				dosimeterToPtu = event.getDosimeterToPtu();
			}
		});
	}
	
	private void init() {
		dosimeters = new HashMap<Integer, Dosimeter>();
		dosimeterToPtu = new HashMap<Integer, Integer>();
		
		String dosimeterLogfile = "APVS-Dosimeter.log";
		// setup the logger
		try {
			logHandler = new FileHandler(dosimeterLogfile, 1000000, 20, true);
			logHandler.setLevel(Level.INFO);
			logHandler.setFormatter(new Formatter() {
				
				@Override
				public String format(LogRecord record) {
					return record.getMessage()+"\n";
				}
			});
		} catch (SecurityException e) {
			System.err.println("Cannot open "+dosimeterLogfile);
		} catch (IOException e) {
			System.err.println("Cannot open "+dosimeterLogfile);
		}
	}

	@Override
	public void run() {

		try {
			while (true) {
				BufferedReader is = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String line;
				while ((line = is.readLine()) != null) {
					Dosimeter dosimeter = DosimeterCoder.decode(line, new Date());
					if (dosimeter == null) continue;
					
					if (dosimeters.put(dosimeter.getSerialNo(), dosimeter) == null) {
						eventBus.fireEvent(new DosimeterSerialNumbersChangedEvent(
								getDosimeterSerialNumbers()));
					}
					eventBus.fireEvent(new DosimeterChangedEvent(dosimeter));
					
					String jsonDosimeter = JsonWriter.objectToJson(dosimeter);
					
					if (logHandler != null) {
						logHandler.publish(new LogRecord(Level.INFO, jsonDosimeter));
					}
					
					Integer ptuId = dosimeterToPtu.get(dosimeter.getSerialNo());
					if (ptuId != null) {
						sendMeasurements(ptuId, dosimeter);
					}
				}
			}
		} catch (IOException e) {
			System.err.println(getClass() + " " + e);
		} finally {
			close();
		}
	}

	private void sendMeasurements(int ptuId, Dosimeter dosimeter) {
		Measurement<Double> rate = new Measurement<Double>(
				ptuId, "Dosimeter Rate", dosimeter.getRate(), "&micro;Sv/h",
				dosimeter.getDate());
		Measurement<Double> dose = new Measurement<Double>(
				ptuId, "Dosimeter Dose", dosimeter.getDose(), "&micro;Sv", dosimeter.getDate());
		eventBus.fireEvent(new MeasurementChangedEvent(rate));
		eventBus.fireEvent(new MeasurementChangedEvent(dose));
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// ignored
		}
		init();
		eventBus.fireEvent(new DosimeterSerialNumbersChangedEvent(null));
	}

	public List<Integer> getDosimeterSerialNumbers() {
		List<Integer> list = new ArrayList<Integer>(dosimeters.keySet());
		Collections.sort(list);
		return list;
	}

	public Dosimeter getDosimeter(int serialNo) {
		return dosimeters.get(serialNo);
	}

	public Map<Integer, Dosimeter> getDosimeterMap() {
		return dosimeters;
	}
}
