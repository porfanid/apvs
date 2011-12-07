package ch.cern.atlas.apvs.server;

import ch.cern.atlas.apvs.eventbus.server.ServerEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

public class APVSServerFactory {
	
	private static APVSServerFactory factory = new APVSServerFactory();
	
	private RemoteEventBus eventBus;
	
	public APVSServerFactory() {
		eventBus = ServerEventBus.getInstance();
	}
	
	public static APVSServerFactory getInstance() {
		return factory;
	}
	
	public RemoteEventBus getEventBus() {
		return eventBus;
	}
	
}
