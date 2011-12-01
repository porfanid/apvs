package ch.cern.atlas.apvs.eventbus.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.atmosphere.gwt.poll.AtmospherePollService;

import ch.cern.atlas.apvs.eventbus.client.EventBusService;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;

@SuppressWarnings("serial")
public class EventBusServiceHandler extends AtmospherePollService implements EventBusService {

	private SuspendInfo info;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("Starting EventBusService...");
	}
	
	@Override
	public void fireEvent(RemoteEvent<?> event) {
		System.err.println("Server: Received event..."+event);
		
		if (info != null) {
			try {
				info.writeAndResume(event);
			} catch (IOException e) {
				System.err.println("Server: Could not write and resume event");
			}
		}
	}

	@Override
	public RemoteEvent<?> getNextEvent() {
		System.err.println("Server: getting next event...");
		info = suspend();
		return null;
	}

}
