package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.FileService;
import ch.cern.atlas.apvs.client.service.FileServiceAsync;
import ch.cern.atlas.apvs.eventbus.client.PollEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.SimpleRemoteEventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;

public class APVSClientFactory implements ClientFactory {
	private SimpleRemoteEventBus eventBus;
	private final PlaceController placeController;
	private final FileServiceAsync fileService = GWT
			.create(FileService.class);

	// private final HelloView helloView = new HelloViewImpl();
	// private final GoodbyeView goodbyeView = new GoodbyeViewImpl();

	public APVSClientFactory() {
		// AtmosphereGWTSerializer serializer =
		// GWT.create(EventSerializer.class);
		// eventBus = new AtmosphereEventBus(serializer);
		eventBus = new PollEventBus();
		placeController = new PlaceController(eventBus);
	}

	@Override
	public RemoteEventBus getEventBus() {
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	@Override
	public FileServiceAsync getFileService() {
		return fileService;
	}
}
