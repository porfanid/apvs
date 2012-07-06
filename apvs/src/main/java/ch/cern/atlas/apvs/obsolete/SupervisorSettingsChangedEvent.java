package ch.cern.atlas.apvs.obsolete;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SupervisorSettingsChangedEvent extends RemoteEvent<SupervisorSettingsChangedEvent.Handler> {

	private static final long serialVersionUID = -1841316380725627939L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onSupervisorSettingsChanged(SupervisorSettingsChangedEvent event);
	}

	private static final Type<SupervisorSettingsChangedEvent.Handler> TYPE = new Type<SupervisorSettingsChangedEvent.Handler>();

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
			SupervisorSettingsChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			SupervisorSettingsChangedEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(SupervisorSettingsChangedEvent.class));
		
		return registration;
	}

	private SupervisorSettings settings;

	public SupervisorSettingsChangedEvent() {
	}

	public SupervisorSettingsChangedEvent(SupervisorSettings settings) {
		this.settings = settings;
	}

	@Override
	public Type<SupervisorSettingsChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public SupervisorSettings getSupervisorSettings() {
		return settings;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSupervisorSettingsChanged(this);
	}

}
