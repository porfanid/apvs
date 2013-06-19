package ch.cern.atlas.apvs.server;

import javax.servlet.http.HttpServletRequest;

import org.atmosphere.gwt.poll.AtmospherePollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.SerializationPolicy;

@SuppressWarnings("serial")
public class ResponsePollService extends AtmospherePollService {
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
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
		// TODO Auto-generated method stub
		log.info("***** "+moduleBaseURL);
		return super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
	}
}
