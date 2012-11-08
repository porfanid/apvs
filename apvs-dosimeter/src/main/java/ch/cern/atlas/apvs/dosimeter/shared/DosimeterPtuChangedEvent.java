package ch.cern.atlas.apvs.dosimeter.shared;

import java.io.Serializable;
import java.util.HashMap;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class DosimeterPtuChangedEvent extends RemoteEvent<DosimeterPtuChangedEvent.Handler> {

	private static final long serialVersionUID = 4835937679842945007L;

	public interface Handler extends Serializable {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onDosimeterPtuChanged(DosimeterPtuChangedEvent event);
	}

	public static final Type<DosimeterPtuChangedEvent.Handler> TYPE = new Type<DosimeterPtuChangedEvent.Handler>();

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
			DosimeterPtuChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(DosimeterPtuChangedEvent.class));
		
		return registration;
	}

	
	private HashMap<String, String> dosimeterToPtu;
	
	public DosimeterPtuChangedEvent() {
	}

	public DosimeterPtuChangedEvent(HashMap<String, String> dosimeterToPtu) {
		this.dosimeterToPtu = dosimeterToPtu;
	}

	@Override
	public Type<DosimeterPtuChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public HashMap<String, String> getDosimeterToPtu() {
		return dosimeterToPtu;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onDosimeterPtuChanged(this);
	}
	
	@Override
	public String toString() {
		return "DosimeterPtuChangedEvent "+dosimeterToPtu.size();
	}
}
