package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ServerSettingsChangedRemoteEvent extends RemoteEvent<ServerSettingsChangedRemoteEvent.Handler> {

	private static final long serialVersionUID = 1233098800577352998L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onServerSettingsChanged(ServerSettingsChangedRemoteEvent event);
	}

	private static final Type<ServerSettingsChangedRemoteEvent.Handler> TYPE = new Type<ServerSettingsChangedRemoteEvent.Handler>();

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
			ServerSettingsChangedRemoteEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(Object src, RemoteEventBus eventBus,
			ServerSettingsChangedRemoteEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(src, ServerSettingsChangedRemoteEvent.class));
		
		return registration;
	}

	private ServerSettings settings;

	public ServerSettingsChangedRemoteEvent() {
	}

	public ServerSettingsChangedRemoteEvent(ServerSettings settings) {
		this.settings = settings;
	}

	@Override
	public Type<ServerSettingsChangedRemoteEvent.Handler> getAssociatedType() {
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
