package ch.cern.atlas.apvs.server;

import java.io.IOException;
import java.util.Stack;

import org.atmosphere.gwt.poll.AtmospherePollService.SuspendInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class ResponseHandler<T> implements
		ValueChangeHandler<T> {

	private ResponsePollService service;
	// Synchronized !!
	private Stack<SuspendInfo> suspendInfo = new Stack<SuspendInfo>();
	private Logger logger = LoggerFactory.getLogger(ResponseHandler.class
			.getName());

	public interface Response<V> {
		public V getValue();
	}

	public ResponseHandler(ResponsePollService service) {
		this.service = service;
	}

	public <V> V respond(
			int currentHashCode, Response<V> response) {

		V object = null;

		if (response != null) {
			try {
				object = response.getValue();
			} catch (NullPointerException e) {
				System.err.println("NPE");
				object = null;
			}
		}
		
		if (object == null) {
			return null;
		}

		if (currentHashCode != object.hashCode()) {
			return object;
		}

		suspendInfo.push(service.suspend());

		return null;
	}

	@Override
	public void onValueChange(ValueChangeEvent<T> event) {
		while (!suspendInfo.isEmpty()) {
			try {
				suspendInfo.pop().writeAndResume(event.getValue());
			} catch (IOException e) {
				logger.error("Failed to write and resume", e);
			}
		}
	}

}
