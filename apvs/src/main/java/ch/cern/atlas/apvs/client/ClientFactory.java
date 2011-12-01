package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.DosimeterServiceAsync;
import ch.cern.atlas.apvs.client.service.PtuServiceAsync;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory {
	
	RemoteEventBus getEventBus();

	PlaceController getPlaceController();
	
	DosimeterServiceAsync getDosimeterService();
	
	PtuServiceAsync getPtuService();
	
	// HelloView getHelloView();
	// GoodbyeView getGoodbyeView();
}
