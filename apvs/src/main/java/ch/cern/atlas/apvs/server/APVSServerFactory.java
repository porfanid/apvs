package ch.cern.atlas.apvs.server;

import ch.cern.atlas.apvs.eventbus.server.ServerEventBus;
import ch.cern.atlas.apvs.eventbus.shared.SimpleRemoteEventBus;

public class APVSServerFactory {
	
	private static APVSServerFactory factory = new APVSServerFactory();
	
	private SimpleRemoteEventBus eventBus;
	
	public APVSServerFactory() {
		eventBus = ServerEventBus.getInstance();
	}
	
	public static APVSServerFactory getInstance() {
		return factory;
	}
	
	public SimpleRemoteEventBus getEventBus() {
		return eventBus;
	}
	
}
