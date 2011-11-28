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

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class DosimeterReader implements Runnable,
		HasValueChangeHandlers<Map<Integer, Dosimeter>> {

	private Socket socket;
	private Map<Integer, Dosimeter> dosimeters = new HashMap<Integer, Dosimeter>();
	private HandlerManager handlerManager = new HandlerManager(this);

	public DosimeterReader(Socket socket) {
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
					dosimeters.put(dosimeter.getSerialNo(), dosimeter);

					ValueChangeEvent.fire(this, dosimeters);
				}
			}
		} catch (IOException e) {
			System.err.println(getClass()+" "+e);
		} finally {
			try {
				close();
			} catch (IOException e) {
				// ignore
			}
		}
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

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Map<Integer, Dosimeter>> handler) {
		return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlerManager.fireEvent(event);
	}

	public void close() throws IOException {
		socket.close();
	}
}
