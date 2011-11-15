package ch.cern.atlas.apvs.client;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {
	
	EventBus getEventBus();

	PlaceController getPlaceController();
	// HelloView getHelloView();
	// GoodbyeView getGoodbyeView();
}
