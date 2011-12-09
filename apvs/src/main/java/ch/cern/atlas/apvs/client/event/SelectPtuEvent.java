package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectPtuEvent extends RemoteEvent<SelectPtuEvent.Handler> {

	private static final long serialVersionUID = 4782769296921555320L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onPtuSelected(SelectPtuEvent event);
	}

	private static final Type<SelectPtuEvent.Handler> TYPE = new Type<SelectPtuEvent.Handler>();

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
			SelectPtuEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			SelectPtuEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(SelectPtuEvent.class));
		
		return registration;
	}

	private Integer ptuId;

	public SelectPtuEvent() {
	}
	
	public SelectPtuEvent(Integer ptuId) {
		this.ptuId = ptuId;
	}

	@Override
	public Type<SelectPtuEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Integer getPtuId() {
		return ptuId;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onPtuSelected(this);
	}
	
	@Override
	public String toString() {
		return "SelectPtuEvent "+ptuId;
	}

}
