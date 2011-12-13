package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.FileService;
import ch.cern.atlas.apvs.client.service.FileServiceAsync;
import ch.cern.atlas.apvs.client.tablet.AboutPanel;
import ch.cern.atlas.apvs.client.tablet.AboutUI;
import ch.cern.atlas.apvs.client.tablet.CameraPanel;
import ch.cern.atlas.apvs.client.tablet.CameraUI;
import ch.cern.atlas.apvs.client.tablet.MainMenuList;
import ch.cern.atlas.apvs.client.tablet.MainMenuUI;
import ch.cern.atlas.apvs.client.tablet.ModelPanel;
import ch.cern.atlas.apvs.client.tablet.ModelUI;
import ch.cern.atlas.apvs.client.tablet.ProcedurePanel;
import ch.cern.atlas.apvs.client.tablet.ProcedureUI;
import ch.cern.atlas.apvs.client.tablet.RadiationMapPanel;
import ch.cern.atlas.apvs.client.tablet.RadiationMapUI;
import ch.cern.atlas.apvs.eventbus.client.PollEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;

public class APVSClientFactory implements ClientFactory {
	private RemoteEventBus eventBus;
	private final PlaceController placeController;
	private final FileServiceAsync fileService = GWT.create(FileService.class);

	private AboutUI aboutView;
	private MainMenuUI homeView;
	private RadiationMapUI radiationMapView;
	private ModelUI modelView;
	private CameraUI cameraView;
	private ProcedureUI procedureView;

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
	public MainMenuUI getHomeView() {
		if (homeView== null) {
			// FIXME BUS
			homeView = new MainMenuList(eventBus, eventBus);
		}
		return homeView;
	}

	@Override
	public AboutUI getAboutView() {
		if (aboutView == null) {
			aboutView = new AboutPanel();
		}

		return aboutView;
	}

	@Override
	public RadiationMapUI getRadiationMapView() {
		if (radiationMapView == null) {
			radiationMapView = new RadiationMapPanel();
		}
		return radiationMapView;
	}

	@Override
	public ModelUI getModelView() {
		if (modelView == null) {
			modelView = new ModelPanel();
		}
		return modelView;
	}

	@Override
	public CameraUI getCameraView() {
		if (cameraView == null) {
			// FIXME eventbus (local)
			cameraView = new CameraPanel(eventBus, eventBus, ch.cern.atlas.apvs.client.CameraView.HELMET);
		}
		return cameraView;
	}

	@Override
	public ProcedureUI getProcedureView() {
		if (procedureView == null) {
			procedureView = new ProcedurePanel();
		}
		return procedureView;
	}

}
