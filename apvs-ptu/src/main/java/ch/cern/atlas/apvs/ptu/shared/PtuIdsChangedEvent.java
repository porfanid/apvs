package ch.cern.atlas.apvs.ptu.shared;

import java.util.List;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class PtuIdsChangedEvent extends RemoteEvent<PtuIdsChangedEvent.Handler> {

	private static final long serialVersionUID = -3179096931692618042L;

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
	public static HandlerRegistration register(RemoteEventBus eventBus,
			PtuIdsChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private List<Integer> ptuIds;
	
	public PtuIdsChangedEvent() {
	}

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
	
	@Override
	public String toString() {
		return "PtuIdsChangedEvent "+ptuIds.size();
	}
}
