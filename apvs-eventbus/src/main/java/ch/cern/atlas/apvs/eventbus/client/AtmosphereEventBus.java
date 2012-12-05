package ch.cern.atlas.apvs.eventbus.client;

import java.util.Iterator;
import java.util.List;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.AtmosphereListener;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.core.client.GWT;

public class AtmosphereEventBus extends RemoteEventBus {
	
	private AtmosphereClient client;

	// NOTE serializer can be null, but note the exception in #284, thrown in "compiled" mode
	public AtmosphereEventBus(AtmosphereGWTSerializer serializer) {
		AtmosphereEventBusListener cometListener = new AtmosphereEventBusListener();
		
		client = new AtmosphereClient(GWT.getModuleBaseURL() + "eventBusComet",
				serializer, cometListener);
		client.start();
		
		getServerEvent();
	}

	private void getServerEvent() {
		// FIXME do an async call, server implements suspend, and answers eventually when a remote event needs
		// to be sent.
	}

	/**
	 * broadcast event and (receive it locally to distribute, below)
	 * 
	 */
	@Override
	public void fireEvent(RemoteEvent<?> event) {
		client.broadcast(event);
	}
	
	/**
	 * broadcast event and (receive it locally to distribute, below)
	 * FIXME source is ignored
	 * 
	 */
	@Override
	public void fireEventFromSource(RemoteEvent<?> event, int uuid) {
		client.broadcast(event);
	}

	public class AtmosphereEventBusListener implements AtmosphereListener {
		
		// atmosphere 1.0
		public void onConnected(int heartbeat, int connectionID) {
			System.err.println("EventBusListener connected (1.0) "+heartbeat+" "+connectionID);
		}
		
		// atmosphere 1.1
		public void onConnected(int heartbeat, String connectionUUID) {
			System.err.println("EventBusListener connected (1.1+) "+heartbeat+" "+connectionUUID);
		}

		@Override
		public void onBeforeDisconnected() {
			System.err.println("EventBusListener before disconnect "+(client != null ? client.getConnectionUUID() : null));
		}

		@Override
		public void onDisconnected() {
			System.err.println("EventBusListener disconnected "+(client != null ? client.getConnectionUUID() : null));
		}

		@Override
		public void onError(Throwable exception, boolean connected) {
			System.err.println("EventBusListener error "+connected+" "+exception+" "+(client != null ? client.getConnectionUUID() : null));
			System.err.println("---------------- error "+exception.getCause());
		}

		@Override
		public void onHeartbeat() {
			System.err.println("EventBusListener heartbeat "+(client != null ? client.getConnectionUUID() : null));
		}

		@Override
		public void onRefresh() {
			System.err.println("EventBusListener refresh "+(client != null ? client.getConnectionUUID() : null));
		}

		/**
		 * handle broadcasted events from other clients
		 */
		@Override
		public void onMessage(List<?> messages) {
			System.err.println("EventBusListener Messages "+messages.size());
			
			for (Iterator<?> i = messages.iterator(); i.hasNext(); ) {
				Object message = i.next();
				if (message instanceof RemoteEvent<?>) {
					RemoteEvent<?> event = (RemoteEvent<?>)message;
					
					// NOTE: also my own needs to be distributed locally
					AtmosphereEventBus.super.fireEvent(event);
				}
			}
		}

		// atmosphere 1.0
		public void onAfterRefresh() {
			System.err.println("EventBusListener after refresh (1.0) "+(client != null ? client.getConnectionUUID() : null));
		}

		// atmosphere 1.1
		public void onAfterRefresh(String connectionUUID) {
			System.err.println("EventBusListener after refresh (1.1+) "+connectionUUID+" "+(client != null ? client.getConnectionUUID() : null));
		}
	}
	
	@Override
	public String toString() {
		return "AtmosphereEventBus";
	}
}
