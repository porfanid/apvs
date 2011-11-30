package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.DosimeterServiceAsync;
import ch.cern.atlas.apvs.client.service.PtuServiceAsync;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {
	
	EventBus getEventBus();

	PlaceController getPlaceController();
	
	DosimeterServiceAsync getDosimeterService();
	
	PtuServiceAsync getPtuService();
	
	// HelloView getHelloView();
	// GoodbyeView getGoodbyeView();
}
