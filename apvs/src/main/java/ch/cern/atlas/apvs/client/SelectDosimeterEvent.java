package ch.cern.atlas.apvs.client;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectDosimeterEvent extends Event<SelectDosimeterEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onDosimeterSelected(SelectDosimeterEvent event);
	}

	private static final Type<SelectDosimeterEvent.Handler> TYPE = new Type<SelectDosimeterEvent.Handler>();

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
			SelectDosimeterEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private final int serialNo;

	public SelectDosimeterEvent(int serialNo) {
		this.serialNo = serialNo;
	}

	@Override
	public Type<SelectDosimeterEvent.Handler> getAssociatedType() {
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
