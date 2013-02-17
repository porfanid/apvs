package ch.cern.atlas.apvs.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.PingService;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class PingServiceImpl extends ResponsePollService implements PingService {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	public PingServiceImpl() {
		log.info("Creating PingService...");
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		log.info("Starting PingService...");
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void ping() {
	}
}
