package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.atmosphere.gwt.poll.AtmospherePollService.SuspendInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class ResponseHandler<T,V> implements ValueChangeHandler<T> {

	private ResponsePollService service;
	// Synchronized !!
	private Vector<InfoAndResponse<V>> delayedResponses = new Vector<InfoAndResponse<V>>();
	private Logger logger = LoggerFactory.getLogger(ResponseHandler.class
			.getName());

	public interface Response<V> {
		public V getValue();
	}
	
	private class InfoAndResponse<S> {
		private SuspendInfo info;
		private Response<S> response;
		private int currentHashCode;

		public InfoAndResponse(SuspendInfo info, Response<S> response, int currentHashCode) {
			this.info = info;
			this.response = response;
			this.currentHashCode = currentHashCode;
		}

		public SuspendInfo getInfo() {
			return info;
		}
		
		public Response<S> getResponse() {
			return response;
		}
		
		public int getCurrentHashCode() {
			return currentHashCode;
		}
	}

	public ResponseHandler(ResponsePollService service) {
		this.service = service;
	}

	public V respond(int currentHashCode, Response<V> response) {

		V object = null;

		try {
			if (response != null) {
				object = response.getValue();
			}

			if (object == null) {
				return null;
			}

			if (currentHashCode != object.hashCode()) {
				return object;
			}
		} catch (NullPointerException e) {
			System.err.println("We assume the call does not work due to a disconnect, we keep you waiting...");
		}

		delayedResponses.add(new InfoAndResponse<V>(service.suspend(), response, currentHashCode));

		return null;
	}

	@Override
	public void onValueChange(ValueChangeEvent<T> event) {
		synchronized (delayedResponses) {
			for (Iterator<InfoAndResponse<V>> i = delayedResponses.iterator(); i.hasNext(); ) {
				InfoAndResponse<V> d = i.next();
				Object o = d.getResponse().getValue();
				if (d.getCurrentHashCode() != o.hashCode()) {
					try {
						d.getInfo().writeAndResume(o);
					} catch (IOException e) {
						logger.error("Failed to write and resume", e);
					}
					i.remove();
				}
			}
		}
	}

}
