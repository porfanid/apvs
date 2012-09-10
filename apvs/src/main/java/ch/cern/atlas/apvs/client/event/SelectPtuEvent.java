package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectPtuEvent extends Event<SelectPtuEvent.Handler> {

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

	public static void fire(EventBus eventBus, String ptuId) {
		eventBus.fireEvent(new SelectPtuEvent(ptuId));
	}	
	
	/**
	 * Register a handler for events on the eventbus.
	 * 
	 * @param eventBus
	 *            the {@link EventBus}
	 * @param handler
	 *            an Handler instance
	 * @return an {@link HandlerRegistration} instance
	 */
	public static HandlerRegistration register(EventBus eventBus,
			SelectPtuEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(EventBus eventBus,
			SelectPtuEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestEvent(SelectPtuEvent.class));
		
		return registration;
	}

	private String ptuId;

	public SelectPtuEvent() {
	}
	
	public SelectPtuEvent(String ptuId) {
		this.ptuId = ptuId;
	}

	@Override
	public Type<SelectPtuEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public String getPtuId() {
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
