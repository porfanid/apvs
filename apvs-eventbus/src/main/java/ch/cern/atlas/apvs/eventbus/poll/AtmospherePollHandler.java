package ch.cern.atlas.apvs.eventbus.poll;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.handler.ReflectorServletProcessor;

public class AtmospherePollHandler extends ReflectorServletProcessor {

	@Override
	public void onStateChange(AtmosphereResourceEvent event) throws IOException {

		if (event.isCancelled() || event.getMessage() == null) {
			return;
		}

		HttpServletRequest request = event.getResource().getRequest();
		if (Boolean.FALSE.equals(request
				.getAttribute(AtmospherePollService.GWT_SUSPENDED))
				|| request.getAttribute(AtmospherePollService.GWT_REQUEST) == null) {

			return;
		}

		boolean success = false;

		try {
			AtmospherePollService.writeResponse(event.getResource(),
					event.getMessage());
			success = true;
		} catch (IllegalArgumentException ex) {
			// the message did not have the same type as the return type of the
			// suspended method
		}
		if (success && event.isSuspended()) {
			request.removeAttribute(AtmospherePollService.GWT_SUSPENDED);
			event.getResource().resume();
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
