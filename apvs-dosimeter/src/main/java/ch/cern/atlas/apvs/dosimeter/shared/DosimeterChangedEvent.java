package ch.cern.atlas.apvs.dosimeter.shared;

import ch.cern.atlas.apvs.domain.Dosimeter;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

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

	public static final Type<DosimeterChangedEvent.Handler> TYPE = new Type<DosimeterChangedEvent.Handler>();

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
			DosimeterChangedEvent.Handler handler) {
		return ((RemoteEventBus)eventBus).addHandler(TYPE, handler);
	}
	
	public static HandlerRegistration subscribe(EventBus eventBus,
			Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		((RemoteEventBus)eventBus).fireEvent(new RequestRemoteEvent(DosimeterChangedEvent.class));
		
		return registration;
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
