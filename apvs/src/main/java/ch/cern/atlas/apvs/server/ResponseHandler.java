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
	private Vector<InfoAndResponse<T,V>> delayedResponses = new Vector<InfoAndResponse<T,V>>();
	private Logger logger = LoggerFactory.getLogger(ResponseHandler.class
			.getName());

	public interface Response<T,V> {
		public V getValue(T object);
	}
	
	private class InfoAndResponse<U,S> {
		private SuspendInfo info;
		private Response<U,S> response;
		private long currentHashCode;

		public InfoAndResponse(SuspendInfo info, Response<U,S> response, long currentHashCode) {
			this.info = info;
			this.response = response;
			this.currentHashCode = currentHashCode;
		}

		public SuspendInfo getInfo() {
			return info;
		}
		
		public Response<U,S> getResponse() {
			return response;
		}
		
		public long getCurrentHashCode() {
			return currentHashCode;
		}
	}

	public ResponseHandler(ResponsePollService service) {
		this.service = service;
	}

	public V respond(long currentHashCode, Response<T,V> response) {

		V object = null;

		try {
			if (response != null) {
				object = response.getValue(null);
			}

			if (object == null) {
				delayedResponses.add(new InfoAndResponse<T,V>(service.suspend(), response, currentHashCode));
				return null;
			}

			if (currentHashCode != object.hashCode()) {
				return object;
			}
		} catch (NullPointerException e) {
			System.err.println("We assume the call does not work due to a disconnect, we keep you waiting...");
			e.printStackTrace(System.err);
		}

		delayedResponses.add(new InfoAndResponse<T,V>(service.suspend(), response, currentHashCode));

		return null;
	}

	@Override
	public void onValueChange(ValueChangeEvent<T> event) {
		synchronized (delayedResponses) {
			for (Iterator<InfoAndResponse<T,V>> i = delayedResponses.iterator(); i.hasNext(); ) {
				InfoAndResponse<T,V> d = i.next();
				Object o = d.getResponse().getValue(event.getValue());
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
