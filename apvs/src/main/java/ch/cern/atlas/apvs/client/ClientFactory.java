package ch.cern.atlas.apvs.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory {
	
	EventBus getEventBus();

	PlaceController getPlaceController();
	// HelloView getHelloView();
	// GoodbyeView getGoodbyeView();
}
