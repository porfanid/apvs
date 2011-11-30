package ch.cern.atlas.apvs.client;

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
	
	private final int ptuId;

	public SelectPtuEvent(int ptuId) {
		this.ptuId = ptuId;
	}

	@Override
	public Type<SelectPtuEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public int getPtuId() {
		return ptuId;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onPtuSelected(this);
	}
}
