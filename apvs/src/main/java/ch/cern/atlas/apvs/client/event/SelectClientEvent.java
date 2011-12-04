package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectClientEvent extends RemoteEvent<SelectClientEvent.Handler> {

	private static final long serialVersionUID = -7796935868246937748L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onClientSelected(SelectClientEvent event);
	}

	private static final Type<SelectClientEvent.Handler> TYPE = new Type<SelectClientEvent.Handler>();

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
			SelectClientEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private long clientId;

	public SelectClientEvent() {
	}
	
	public SelectClientEvent(long clientId) {
		this.clientId = clientId;
	}

	@Override
	public Type<SelectClientEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public long getClientId() {
		return clientId;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onClientSelected(this);
	}
	
	@Override
	public String toString() {
		return "SelectClientEvent "+clientId;
	}

}
