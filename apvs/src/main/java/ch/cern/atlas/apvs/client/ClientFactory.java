package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.FileServiceAsync;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory {
	
	RemoteEventBus getEventBus();

	PlaceController getPlaceController();
	
	FileServiceAsync getFileService();
		
	// HelloView getHelloView();
	// GoodbyeView getGoodbyeView();
}
