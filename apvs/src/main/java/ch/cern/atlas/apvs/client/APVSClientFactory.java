package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.DosimeterService;
import ch.cern.atlas.apvs.client.service.DosimeterServiceAsync;
import ch.cern.atlas.apvs.client.service.PtuService;
import ch.cern.atlas.apvs.client.service.PtuServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class APVSClientFactory implements ClientFactory {
	private final EventBus eventBus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(
			eventBus);
	private final DosimeterServiceAsync dosimeterService = GWT.create(DosimeterService.class);
	private final PtuServiceAsync ptuService = GWT.create(PtuService.class);
	
//	private final HelloView helloView = new HelloViewImpl();
//	private final GoodbyeView goodbyeView = new GoodbyeViewImpl();

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	@Override
	public DosimeterServiceAsync getDosimeterService() {
		return dosimeterService;
	}

	@Override
	public PtuServiceAsync getPtuService() {
		return ptuService;
	}
}
