package ch.cern.atlas.apvs.eventbus.server;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.atmosphere.gwt.poll.AtmospherePollService;

import ch.cern.atlas.apvs.eventbus.client.EventBusService;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;

@SuppressWarnings("serial")
public class EventBusServiceHandler extends AtmospherePollService implements
		EventBusService {

	private SuspendInfo info;
	private ServerEventBus eventBus;
	private ConcurrentLinkedQueue<RemoteEvent<?>> eventQueue = new ConcurrentLinkedQueue<RemoteEvent<?>>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("Starting EventBusService...");

		eventBus = ServerEventBus.getInstance();
		eventBus.setEventBusServiceHandler(this);
	}

	@Override
	public void fireEvent(RemoteEvent<?> event) {
		System.err.println("Server: Received event..." + event);
		sendToRemote(event);

		eventBus.forwardEvent(event);
	}

	@Override
	public RemoteEvent<?> getNextEvent() {
		RemoteEvent<?> event = eventQueue.poll();
		if (event != null) {
			System.err.println("Server: getting next event..." + event);
			return event;
		}
		System.err.println("Server: getting next event...");
		info = suspend();
		return null;
	}

	public void forwardEvent(RemoteEvent<?> event) {
		// System.err.println("Server: Forward event..."+event);
		sendToRemote(event);
	}

	private void sendToRemote(RemoteEvent<?> event) {
		if (event == null) {
			System.err.println("*S**S*S* event is null");
			return;
		}

		// FIXME we need to make sure that ALL events get routed to ALL
		// receivers
		// we need a queue per requester on uuid
		// need to empty those queueus sometimes (timer)
		// keep an info object per uuid
		eventQueue.add(event);

		purgeQueue();
	}

	// FIXME the call for next event should ask for list of.
	private void purgeQueue() {
		if (info != null) {
			RemoteEvent<?> event = eventQueue.poll();
			if (event != null) {
				try {
					info.writeAndResume(event);
					System.err.println("Sending event..." + event);
					info = null;
				} catch (IOException e) {
					System.err
							.println("Server: Could not write and resume event "
									+ e);
				}
			}
		}

	}

}
