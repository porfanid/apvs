package ch.cern.atlas.apvs.eventbus.server;

import java.util.logging.Logger;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

public class ServerEventBus extends RemoteEventBus {

	private static ServerEventBus instance;
	private static Logger log = Logger.getLogger(ServerEventBus.class.getName());

	private EventBusServiceHandler eventBusServiceHandler;

	public static ServerEventBus getInstance() {
		if (instance == null) {
			instance = new ServerEventBus();
		}
		return instance;
	}

	private ServerEventBus() {
	}

	public void setEventBusServiceHandler(
			EventBusServiceHandler eventBusServiceHandler) {
		this.eventBusServiceHandler = eventBusServiceHandler;
	}

	@Override
	public void fireEvent(RemoteEvent<?> event) {
		log.info("Fire event "+event.getClass());
		doFire(event);
	}

	@Override
	public void fireEventFromSource(RemoteEvent<?> event,
			int uuid) {
		log.info("Fire event "+event.getClass()+" "+uuid);
		doFire(event);
	}

	private void doFire(RemoteEvent<?> event) {
		// send out locally
		super.fireEvent(event);

		// send to remote
		if (eventBusServiceHandler != null) {
			eventBusServiceHandler.forwardEvent(event);
		}
	}

	public void forwardEvent(RemoteEvent<?> event) {
		// Only forward events that are not from us
		if (event.getEventBusUUID() != getUUID()) {
			super.fireEvent(event);
		}
	}

}
