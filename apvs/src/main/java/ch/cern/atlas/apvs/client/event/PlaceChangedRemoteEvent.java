package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.places.SharedPlace;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class PlaceChangedRemoteEvent extends RemoteEvent<PlaceChangedRemoteEvent.Handler> {

	private static final long serialVersionUID = -1860914796613543708L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onPlaceChanged(PlaceChangedRemoteEvent event);
	}

	private static final Type<PlaceChangedRemoteEvent.Handler> TYPE = new Type<PlaceChangedRemoteEvent.Handler>();

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
			PlaceChangedRemoteEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			PlaceChangedRemoteEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(PlaceChangedRemoteEvent.class));
		
		return registration;
	}

	private String ptuId;
	private SharedPlace place;

	public PlaceChangedRemoteEvent() {
	}
	
	public PlaceChangedRemoteEvent(String ptuId, SharedPlace place) {
		this.ptuId = ptuId;
		this.place = place;
	}

	@Override
	public Type<PlaceChangedRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public String getPtuId() {
		return ptuId;
	}
	
	public SharedPlace getPlace() {
		return place;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onPlaceChanged(this);
	}
	
	@Override
	public String toString() {
		return "PlaceChangedEvent "+place;
	}

}
