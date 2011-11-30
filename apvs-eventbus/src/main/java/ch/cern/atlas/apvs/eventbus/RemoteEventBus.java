package ch.cern.atlas.apvs.eventbus;

import java.io.Serializable;
import java.util.List;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.AtmosphereListener;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.SimpleEventBus;

// FIXME maybe resetable event bus ?
public class RemoteEventBus extends SimpleEventBus {

	private AtmosphereClient client;

	public RemoteEventBus() {
		EventBusCometListener cometListener = new EventBusCometListener();
		
		AtmosphereGWTSerializer serializer = GWT.create(EventSerializer.class);
		client = new AtmosphereClient(GWT.getModuleBaseURL() + "eventBusComet",
				serializer, cometListener);
		client.start();
	}
	
	// FIXME overload fireEvent methodS


	public class EventBusCometListener implements AtmosphereListener {

		@Override
		public void onConnected(int heartbeat, int connectionID) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onBeforeDisconnected() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(Throwable exception, boolean connected) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onHeartbeat() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMessage(List<? extends Serializable> messages) {
			// TODO Auto-generated method stub
			
		}

	}
}
