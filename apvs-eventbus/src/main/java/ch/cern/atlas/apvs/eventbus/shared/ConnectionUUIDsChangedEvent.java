package ch.cern.atlas.apvs.eventbus.shared;

import java.util.List;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ConnectionUUIDsChangedEvent extends RemoteEvent<ConnectionUUIDsChangedEvent.Handler> {

	private static final long serialVersionUID = -360451669494831303L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onConnectionUUIDchanged(ConnectionUUIDsChangedEvent event);
	}

	private static final Type<ConnectionUUIDsChangedEvent.Handler> TYPE = new Type<ConnectionUUIDsChangedEvent.Handler>();

	/**
	 * Register a handler for events on the eventbus.
	 * 
	 * @param eventBus
	 *            the {@link EventBus}
	 * @param handler
	 *            an Handler instance
	 * @return an {@link HandlerRegistration} instance
	 */
	public static HandlerRegistration register(RemoteEventBus eventBus,
			ConnectionUUIDsChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	public static HandlerRegistration subscribe(RemoteEventBus eventBus, Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(ConnectionUUIDsChangedEvent.class));
		
		return registration;
	}

	
	private List<String> uuids;
	
	public ConnectionUUIDsChangedEvent() {
	}

	public ConnectionUUIDsChangedEvent(List<String> uuids) {
		this.uuids = uuids;
	}

	@Override
	public Type<ConnectionUUIDsChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public List<String> getConnectionUUIDs() {
		return uuids;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onConnectionUUIDchanged(this);
	}
	
	@Override
	public String toString() {
		return "ConnectionUUIDsChangedEvent "+uuids.size();
	}
}
