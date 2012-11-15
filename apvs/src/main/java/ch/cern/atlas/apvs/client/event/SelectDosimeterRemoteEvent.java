package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectDosimeterRemoteEvent extends RemoteEvent<SelectDosimeterRemoteEvent.Handler> {

	private static final long serialVersionUID = 3684090069329583327L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onDosimeterSelected(SelectDosimeterRemoteEvent event);
	}

	private static final Type<SelectDosimeterRemoteEvent.Handler> TYPE = new Type<SelectDosimeterRemoteEvent.Handler>();

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
			SelectDosimeterRemoteEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private int serialNo;
	
	public SelectDosimeterRemoteEvent() {
	}

	public SelectDosimeterRemoteEvent(int serialNo) {
		this.serialNo = serialNo;
	}

	@Override
	public Type<SelectDosimeterRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public int getSerialNo() {
		return serialNo;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onDosimeterSelected(this);
	}
}
