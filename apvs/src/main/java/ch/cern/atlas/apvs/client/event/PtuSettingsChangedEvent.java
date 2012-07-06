package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class PtuSettingsChangedEvent extends RemoteEvent<PtuSettingsChangedEvent.Handler> {

	private static final long serialVersionUID = -9142224229884250487L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onPtuSettingsChanged(PtuSettingsChangedEvent event);
	}

	private static final Type<PtuSettingsChangedEvent.Handler> TYPE = new Type<PtuSettingsChangedEvent.Handler>();

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
			PtuSettingsChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			PtuSettingsChangedEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(PtuSettingsChangedEvent.class));
		
		return registration;
	}

	private PtuSettings settings;

	public PtuSettingsChangedEvent() {
	}

	public PtuSettingsChangedEvent(PtuSettings settings) {
		this.settings = settings;
	}

	@Override
	public Type<PtuSettingsChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public PtuSettings getPtuSettings() {
		return settings;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onPtuSettingsChanged(this);
	}

}
