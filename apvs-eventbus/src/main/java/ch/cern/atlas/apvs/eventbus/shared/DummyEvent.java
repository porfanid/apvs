package ch.cern.atlas.apvs.eventbus.shared;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class DummyEvent extends RemoteEvent<DummyEvent.Handler> {

	private static final long serialVersionUID = -1474531239923545673L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onServerSettingsChanged(DummyEvent event);
	}

	private static final Type<DummyEvent.Handler> TYPE = new Type<DummyEvent.Handler>();

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
			DummyEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			DummyEvent.Handler handler) throws SerializationException {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(DummyEvent.class));
		
		return registration;
	}

	public DummyEvent() {
	}

	@Override
	public Type<DummyEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onServerSettingsChanged(this);
	}

}
