package ch.cern.atlas.apvs.server;

import org.atmosphere.gwt.poll.AtmospherePollService;

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
}
