package ch.cern.atlas.apvs.ptu.server;

import java.util.List;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class PtuIdsChangedEvent extends Event<PtuIdsChangedEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onPtuIdsChanged(PtuIdsChangedEvent event);
	}

	private static final Type<PtuIdsChangedEvent.Handler> TYPE = new Type<PtuIdsChangedEvent.Handler>();

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
			PtuIdsChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private final List<Integer> ptuIds;

	public PtuIdsChangedEvent(List<Integer> ptuIds) {
		this.ptuIds = ptuIds;
	}

	@Override
	public Type<PtuIdsChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public List<Integer> getPtuIds() {
		return ptuIds;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onPtuIdsChanged(this);
	}
}
