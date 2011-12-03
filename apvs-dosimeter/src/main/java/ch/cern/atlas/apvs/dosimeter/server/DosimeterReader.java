package ch.cern.atlas.apvs.dosimeter.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.domain.Dosimeter;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterChangedEvent;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterSerialNumbersChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

public class DosimeterReader implements Runnable {

	private RemoteEventBus eventBus;
	private Socket socket;
	private Map<Integer, Dosimeter> dosimeters = new HashMap<Integer, Dosimeter>();

	public DosimeterReader(RemoteEventBus eventBus, Socket socket) {
		this.eventBus = eventBus;
		this.socket = socket;
	}

	@Override
	public void run() {

		try {
			while (true) {
				BufferedReader is = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String line;
				while ((line = is.readLine()) != null) {
					Dosimeter dosimeter = DosimeterCoder.decode(line);
					
					if (dosimeters.put(dosimeter.getSerialNo(), dosimeter) == null) {
						eventBus.fireEvent(new DosimeterSerialNumbersChangedEvent(new ArrayList<Integer>(dosimeters.keySet())));	
					}
					eventBus.fireEvent(new DosimeterChangedEvent(dosimeter));
				}
			}
		} catch (IOException e) {
			System.err.println(getClass()+" "+e);
		} finally {
			close();
		}
	}

	public void close() {
		try {
			socket.close();			
		} catch (IOException e) {
			// ignored
		}
		
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
