package ch.cern.atlas.apvs.ptu.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import ch.cern.atlas.apvs.domain.Measurement;

import com.cedarsoftware.util.io.JsonReader;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class PtuReader implements Runnable,
		HasValueChangeHandlers<Measurement<?>> {

	private Socket socket;
	private HandlerManager handlerManager = new HandlerManager(this);

	public PtuReader(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		try {
			while (true) {
/*
				BufferedReader is = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				// FIXME, may not contain end of line chars
				String line;
				while ((line = is.readLine()) != null) {
//					Dosimeter dosimeter = DosimeterCoder.decode(line);
//					dosimeters.put(dosimeter.getSerialNo(), dosimeter);
					System.err.println("***"+line);
					
//					Measurement<Double> measurement = new Measurement<Double>();
					
//					ValueChangeEvent.fire(this, measurement);
				}
				*/
				JsonReader reader = new JsonReader(socket.getInputStream(), false);
				while (true) {
					System.err.println(reader.readObject());
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

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Measurement<?>> handler) {
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
