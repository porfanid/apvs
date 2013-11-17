package ch.cern.atlas.apvs.server;

import javax.servlet.http.HttpServletRequest;

import ch.cern.atlas.apvs.eventbus.poll.AtmospherePollService;
import ch.cern.atlas.apvs.eventbus.server.ServerSerialization;

import com.google.gwt.user.server.rpc.SerializationPolicy;

@SuppressWarnings("serial")
public class ResponsePollService extends AtmospherePollService {
	
	public ResponsePollService() {
	}
	
	@Override
	public SuspendInfo suspend() {
		return super.suspend();
	}

	@Override
	public SuspendInfo suspend(long timeout) {
		return super.suspend(timeout);
	}
	
	@Override
	protected SerializationPolicy doGetSerializationPolicy(
			HttpServletRequest request, String moduleBaseURL, String strongName) {
		return super.doGetSerializationPolicy(request,
				ServerSerialization.getModuleBaseURL(request, moduleBaseURL),
				strongName);
	}
	
	protected boolean isSupervisor() {
		Boolean isSupervisor = (Boolean) getThreadLocalRequest().getSession(
				true).getAttribute("SUPERVISOR");
		return isSupervisor != null ? isSupervisor : false;
	}
}
