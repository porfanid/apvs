package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.ServerSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ServerSettingsChangedEvent extends RemoteEvent<ServerSettingsChangedEvent.Handler> {

	private static final long serialVersionUID = 1233098800577352998L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onServerSettingsChanged(ServerSettingsChangedEvent event);
	}

	private static final Type<ServerSettingsChangedEvent.Handler> TYPE = new Type<ServerSettingsChangedEvent.Handler>();

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
			ServerSettingsChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			ServerSettingsChangedEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(ServerSettingsChangedEvent.class));
		
		return registration;
	}

	private ServerSettings settings;

	public ServerSettingsChangedEvent() {
	}

	public ServerSettingsChangedEvent(ServerSettings settings) {
		this.settings = settings;
	}

	@Override
	public Type<ServerSettingsChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public ServerSettings getServerSettings() {
		return settings;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onServerSettingsChanged(this);
	}

}
