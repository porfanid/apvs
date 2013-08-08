package ch.cern.atlas.apvs.client.event;

import java.io.Serializable;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectClientRemoteEvent extends RemoteEvent<SelectClientRemoteEvent.Handler> {

	private static final long serialVersionUID = -7796935868246937748L;

	//NOTE: implements IsSerializable in case serialization file cannot be found
	public interface Handler extends Serializable, IsSerializable {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onClientSelected(SelectClientRemoteEvent event);
	}

	private static final Type<SelectClientRemoteEvent.Handler> TYPE = new Type<SelectClientRemoteEvent.Handler>();

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
			SelectClientRemoteEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private long clientId;

	public SelectClientRemoteEvent() {
	}
	
	public SelectClientRemoteEvent(long clientId) {
		this.clientId = clientId;
	}

	@Override
	public Type<SelectClientRemoteEvent.Handler> getAssociatedType() {
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
