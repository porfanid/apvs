package ch.cern.atlas.apvs.server;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class APVSServerFactory {
	
	private static APVSServerFactory factory = new APVSServerFactory();
	
	private EventBus eventBus;
	
	public APVSServerFactory() {
		eventBus = new SimpleEventBus();
	}
	
	public static APVSServerFactory getInstance() {
		return factory;
	}
	
	public EventBus getEventBus() {
		return eventBus;
	}
	
}
