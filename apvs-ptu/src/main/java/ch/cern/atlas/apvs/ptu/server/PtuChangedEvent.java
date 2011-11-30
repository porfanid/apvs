package ch.cern.atlas.apvs.ptu.server;

import ch.cern.atlas.apvs.domain.Ptu;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class PtuChangedEvent extends Event<PtuChangedEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onPtuChanged(PtuChangedEvent event);
	}

	private static final Type<PtuChangedEvent.Handler> TYPE = new Type<PtuChangedEvent.Handler>();

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
			PtuChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private final Ptu ptu;

	public PtuChangedEvent(Ptu ptu) {
		this.ptu = ptu;
	}

	@Override
	public Type<PtuChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Ptu getPtu() {
		return ptu;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onPtuChanged(this);
	}
}
