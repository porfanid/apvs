package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.DosimeterServiceAsync;
import ch.cern.atlas.apvs.client.service.PtuServiceAsync;
import ch.cern.atlas.apvs.eventbus.shared.SimpleRemoteEventBus;

import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory {
	
	// FIXME change to RemoteEventBus
	SimpleRemoteEventBus getEventBus();

	PlaceController getPlaceController();
	
	DosimeterServiceAsync getDosimeterService();
	
	PtuServiceAsync getPtuService();
	
	// HelloView getHelloView();
	// GoodbyeView getGoodbyeView();
}
