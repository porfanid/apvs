package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.FileService;
import ch.cern.atlas.apvs.client.service.FileServiceAsync;
import ch.cern.atlas.apvs.client.tablet.AboutView;
import ch.cern.atlas.apvs.client.tablet.AboutViewGwtImpl;
import ch.cern.atlas.apvs.client.tablet.ModelView;
import ch.cern.atlas.apvs.client.tablet.ModelViewImpl;
import ch.cern.atlas.apvs.client.tablet.RadiationMapView;
import ch.cern.atlas.apvs.client.tablet.RadiationMapViewImpl;
import ch.cern.atlas.apvs.client.tablet.ShowCaseListView;
import ch.cern.atlas.apvs.client.tablet.ShowCaseListViewGwtImpl;
import ch.cern.atlas.apvs.eventbus.client.PollEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;

public class APVSClientFactory implements ClientFactory {
	private RemoteEventBus eventBus;
	private final PlaceController placeController;
	private final FileServiceAsync fileService = GWT.create(FileService.class);

	private AboutView aboutView;
	private ShowCaseListView homeViewImpl;
	private RadiationMapView radiationMapView;
	private ModelView modelView;

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

	@Override
	public ShowCaseListView getHomeView() {
		if (homeViewImpl == null) {
			homeViewImpl = new ShowCaseListViewGwtImpl();
		}
		return homeViewImpl;
	}

	@Override
	public AboutView getAboutView() {
		if (aboutView == null) {
			aboutView = new AboutViewGwtImpl();
		}

		return aboutView;
	}

	@Override
	public RadiationMapView getRadiationMapView() {
		if (radiationMapView == null) {
			radiationMapView = new RadiationMapViewImpl();
		}
		return radiationMapView;
	}

	@Override
	public ModelView getModelView() {
		if (modelView == null) {
			modelView = new ModelViewImpl();
		}
		return modelView;
	}

}
