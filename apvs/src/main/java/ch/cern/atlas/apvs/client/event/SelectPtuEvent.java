package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectPtuEvent extends Event<SelectPtuEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 * @throws SerializationException 
		 */
		void onPtuSelected(SelectPtuEvent event);
	}

	private static final Type<SelectPtuEvent.Handler> TYPE = new Type<SelectPtuEvent.Handler>();

	public static void fire(EventBus eventBus, Device device) {
		eventBus.fireEvent(new SelectPtuEvent(device));
	}	
	
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
			SelectPtuEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(Object target, EventBus eventBus,
			SelectPtuEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestEvent(SelectPtuEvent.class, target.getClass()));
		
		return registration;
	}

	private Device device;

	public SelectPtuEvent() {
	}
	
	public SelectPtuEvent(Device device) {
		this.device = device;
	}

	@Override
	public Type<SelectPtuEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Device getPtu() {
		return device;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onPtuSelected(this);
	}
	
	@Override
	public String toString() {
		return "SelectPtuEvent "+device.getName();
	}

}
