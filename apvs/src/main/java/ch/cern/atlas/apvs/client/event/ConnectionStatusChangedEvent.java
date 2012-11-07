package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ConnectionStatusChangedEvent extends
		RemoteEvent<ConnectionStatusChangedEvent.Handler> {

	private static final long serialVersionUID = 8865199851228810365L;

	public enum ConnectionType {
		audio("Audio Status"), video("Video Status"), daq("DAQ Status"), dosimeter(
				"Dosimeter Status"), database("Database Status");

		private String s;

		private ConnectionType(String s) {
			this.s = s;
		}

		public String getString() {
			return s;
		}
	}

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onConnectionStatusChanged(ConnectionStatusChangedEvent event);
	}

	private static final Type<ConnectionStatusChangedEvent.Handler> TYPE = new Type<ConnectionStatusChangedEvent.Handler>();

	public static void fire(RemoteEventBus eventBus, ConnectionType type,
			boolean ok) {
		eventBus.fireEvent(new ConnectionStatusChangedEvent(type, ok));
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
	public static HandlerRegistration register(RemoteEventBus eventBus,
			ConnectionStatusChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			ConnectionStatusChangedEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);

		eventBus.fireEvent(new RequestRemoteEvent(
				ConnectionStatusChangedEvent.class));

		return registration;
	}

	private ConnectionType connection;
	private boolean ok;

	public ConnectionStatusChangedEvent() {
	}

	public ConnectionStatusChangedEvent(ConnectionType connection, boolean ok) {
		this.connection = connection;
		this.ok = ok;
	}

	@Override
	public Type<ConnectionStatusChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public ConnectionType getConnection() {
		return connection;
	}

	public boolean isOk() {
		return ok;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onConnectionStatusChanged(this);
	}

}
