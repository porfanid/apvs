package ch.cern.atlas.apvs.client;

import org.atmosphere.gwt.client.AtmosphereGWTSerializer;

import ch.cern.atlas.apvs.client.service.DosimeterService;
import ch.cern.atlas.apvs.client.service.DosimeterServiceAsync;
import ch.cern.atlas.apvs.client.service.PtuService;
import ch.cern.atlas.apvs.client.service.PtuServiceAsync;
import ch.cern.atlas.apvs.eventbus.client.AtmosphereEventBus;
import ch.cern.atlas.apvs.eventbus.shared.SimpleRemoteEventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;

public class APVSClientFactory implements ClientFactory {
	private SimpleRemoteEventBus eventBus;
	private final PlaceController placeController = new PlaceController(
			eventBus);
	private final DosimeterServiceAsync dosimeterService = GWT.create(DosimeterService.class);
	private final PtuServiceAsync ptuService = GWT.create(PtuService.class);
	
//	private final HelloView helloView = new HelloViewImpl();
//	private final GoodbyeView goodbyeView = new GoodbyeViewImpl();
	
	public APVSClientFactory() {
		AtmosphereGWTSerializer serializer = GWT.create(EventSerializer.class);
		eventBus = new AtmosphereEventBus(serializer);
	}

	@Override
	public SimpleRemoteEventBus getEventBus() {
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
