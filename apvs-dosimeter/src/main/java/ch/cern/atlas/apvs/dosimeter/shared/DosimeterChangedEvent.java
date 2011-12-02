package ch.cern.atlas.apvs.dosimeter.shared;

import ch.cern.atlas.apvs.domain.Dosimeter;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class DosimeterChangedEvent extends RemoteEvent<DosimeterChangedEvent.Handler> {

	private static final long serialVersionUID = 8398639252531762039L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onDosimeterChanged(DosimeterChangedEvent event);
	}

	private static final Type<DosimeterChangedEvent.Handler> TYPE = new Type<DosimeterChangedEvent.Handler>();

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
			DosimeterChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private Dosimeter dosimeter;
	
	public DosimeterChangedEvent() {
	}

	public DosimeterChangedEvent(Dosimeter dosimeter) {
		this.dosimeter = dosimeter;
	}

	@Override
	public Type<DosimeterChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Dosimeter getDosimeter() {
		return dosimeter;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onDosimeterChanged(this);
	}
	
	@Override
	public String toString() {
		return "DosimeterChangedEvent "+dosimeter;
	}
}
