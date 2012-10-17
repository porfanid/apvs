package ch.cern.atlas.apvs.ptu.shared;

import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class EventChangedEvent extends RemoteEvent<EventChangedEvent.Handler> {

	private static final long serialVersionUID = -2072221604480235570L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onEventChanged(EventChangedEvent event);
	}

	private static final Type<EventChangedEvent.Handler> TYPE = new Type<EventChangedEvent.Handler>();

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
			EventChangedEvent.Handler handler) {
		return ((RemoteEventBus)eventBus).addHandler(TYPE, handler);
	}
		
	private Event event;
	
	public EventChangedEvent() {
	}

	public EventChangedEvent(Event event) {
		this.event = event;
	}

	@Override
	public Type<EventChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Event getEvent() {
		return event;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onEventChanged(this);
	}
	
	@Override
	public String toString() {
		return "EventChangedEvent "+(event != null ? event.getPtuId()+" "+event.getName() : "null");
	}
}
