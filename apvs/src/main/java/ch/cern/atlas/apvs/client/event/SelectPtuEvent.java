package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

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
	
	private int ptuId;

	public SelectPtuEvent() {
	}
	
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
