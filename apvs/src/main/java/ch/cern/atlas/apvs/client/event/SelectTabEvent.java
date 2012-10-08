package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectTabEvent extends Event<SelectTabEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onTabSelected(SelectTabEvent event);
	}

	private static final Type<SelectTabEvent.Handler> TYPE = new Type<SelectTabEvent.Handler>();

	public static void fire(EventBus eventBus, String tab) {
		eventBus.fireEvent(new SelectTabEvent(tab));
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
			SelectTabEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(EventBus eventBus,
			SelectTabEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestEvent(SelectTabEvent.class));
		
		return registration;
	}

	private String tab;

	public SelectTabEvent() {
	}
	
	public SelectTabEvent(String tab) {
		this.tab = tab;
	}

	@Override
	public Type<SelectTabEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public String getTab() {
		return tab;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onTabSelected(this);
	}
	
	@Override
	public String toString() {
		return "SelectTabEvent "+tab;
	}

}
