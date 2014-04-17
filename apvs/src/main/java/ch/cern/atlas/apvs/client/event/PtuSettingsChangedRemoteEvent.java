package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class PtuSettingsChangedRemoteEvent extends RemoteEvent<PtuSettingsChangedRemoteEvent.Handler> {

	private static final long serialVersionUID = -9142224229884250487L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onPtuSettingsChanged(PtuSettingsChangedRemoteEvent event);
	}

	private static final Type<PtuSettingsChangedRemoteEvent.Handler> TYPE = new Type<PtuSettingsChangedRemoteEvent.Handler>();

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
			PtuSettingsChangedRemoteEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(Object src, RemoteEventBus eventBus,
			PtuSettingsChangedRemoteEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(src, PtuSettingsChangedRemoteEvent.class));
		
		return registration;
	}

	private PtuSettings settings;

	public PtuSettingsChangedRemoteEvent() {
	}

	public PtuSettingsChangedRemoteEvent(PtuSettings settings) {
		this.settings = settings;
	}

	@Override
	public Type<PtuSettingsChangedRemoteEvent.Handler> getAssociatedType() {
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
