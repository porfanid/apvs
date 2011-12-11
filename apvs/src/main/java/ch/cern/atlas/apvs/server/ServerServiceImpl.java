package ch.cern.atlas.apvs.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.client.service.ServerService;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class ServerServiceImpl extends ResponsePollService implements
		ServerService {

	private RemoteEventBus eventBus;

	public ServerServiceImpl() {
		System.out.println("Creating ServerService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		System.out.println("Starting ServerService...");

		ServerSettingsStorage.getInstance(eventBus);
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public boolean isReady() {

		return true;
	}
}
