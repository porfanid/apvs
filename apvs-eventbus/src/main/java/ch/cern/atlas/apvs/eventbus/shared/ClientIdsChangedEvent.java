package ch.cern.atlas.apvs.eventbus.shared;

import java.util.List;


import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ClientIdsChangedEvent extends RemoteEvent<ClientIdsChangedEvent.Handler> {

	private static final long serialVersionUID = 1483151980311290676L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onClientIdsChanged(ClientIdsChangedEvent event);
	}

	private static final Type<ClientIdsChangedEvent.Handler> TYPE = new Type<ClientIdsChangedEvent.Handler>();

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
			ClientIdsChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	public static HandlerRegistration subscribe(RemoteEventBus eventBus, Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(ClientIdsChangedEvent.class));
		
		return registration;
	}

	
	private List<Long> clientIds;
	
	public ClientIdsChangedEvent() {
	}

	public ClientIdsChangedEvent(List<Long> clientIds) {
		this.clientIds = clientIds;
	}

	@Override
	public Type<ClientIdsChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public List<Long> getClientIds() {
		return clientIds;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onClientIdsChanged(this);
	}
	
	@Override
	public String toString() {
		return "ClientIdsChangedEvent "+clientIds.size();
	}
}
