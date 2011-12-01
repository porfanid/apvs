package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class NavigateStepEvent extends RemoteEvent<NavigateStepEvent.Handler> {

	private static final long serialVersionUID = -7366676770104318404L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onNavigateStep(NavigateStepEvent event);
	}

	private static final Type<NavigateStepEvent.Handler> TYPE = new Type<NavigateStepEvent.Handler>();

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
			NavigateStepEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	public static enum Navigation {
		START, PREVIOUS, NEXT;
	}

	private Navigation navigation;
	
	public NavigateStepEvent() {
	}

	public NavigateStepEvent(Navigation selection) {
		this.navigation = selection;
	}

	@Override
	public Type<NavigateStepEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Navigation getNavigation() {
		return navigation;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onNavigateStep(this);
	}
}
