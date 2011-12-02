package ch.cern.atlas.apvs.eventbus.server;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.SimpleRemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.UUID;

public class ServerEventBus extends SimpleRemoteEventBus {
	
	private static ServerEventBus instance;

	private long eventBusUUID = UUID.uuidLong(8);
	private EventBusServiceHandler eventBusServiceHandler;

	public static ServerEventBus getInstance() {
		if (instance == null) {
			instance = new ServerEventBus();
		}
		return instance;
	}
	
	private ServerEventBus() {
	}
	
	public void setEventBusServiceHandler(EventBusServiceHandler eventBusServiceHandler) {
		this.eventBusServiceHandler = eventBusServiceHandler;		
	}

	@Override
	public void fireEvent(RemoteEvent<?> event) {
		doFire(event);
	}

	@Override
	public void fireEventFromSource(RemoteEvent<?> event, int uuid) {
		doFire(event);
	}

	private void doFire(RemoteEvent<?> event) {
		setEventBusUuidOfEvent(event, eventBusUUID);
		
		// send out locally
		super.fireEvent(event);

		// send to remote
		if (eventBusServiceHandler != null) {
			eventBusServiceHandler.forwardEvent(event);
		}
	}

	public void forwardEvent(RemoteEvent<?> event) {
		// Only forward events that are not from us
		if (event.getEventBusUUID() != eventBusUUID) {
			super.fireEvent(event);
		}
	}

}
