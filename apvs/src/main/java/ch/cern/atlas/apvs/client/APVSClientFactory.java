package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.FileService;
import ch.cern.atlas.apvs.client.service.FileServiceAsync;
import ch.cern.atlas.apvs.client.tablet.CameraPanel;
import ch.cern.atlas.apvs.client.tablet.CameraUI;
import ch.cern.atlas.apvs.client.tablet.ImagePanel;
import ch.cern.atlas.apvs.client.tablet.ImageUI;
import ch.cern.atlas.apvs.client.tablet.MainMenuList;
import ch.cern.atlas.apvs.client.tablet.MainMenuUI;
import ch.cern.atlas.apvs.client.tablet.ModelPanel;
import ch.cern.atlas.apvs.client.tablet.ModelUI;
import ch.cern.atlas.apvs.client.tablet.ProcedureMenuPanel;
import ch.cern.atlas.apvs.client.tablet.ProcedureMenuUI;
import ch.cern.atlas.apvs.client.tablet.ProcedureNavigator;
import ch.cern.atlas.apvs.client.tablet.ProcedurePanel;
import ch.cern.atlas.apvs.client.tablet.ProcedureUI;
import ch.cern.atlas.apvs.eventbus.client.PollEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;

public class APVSClientFactory implements ClientFactory {
	private RemoteEventBus remoteEventBus;
	private RemoteEventBus localEventBus;
	private final PlaceController placeController;
	private final FileServiceAsync fileService = GWT.create(FileService.class);

	private MainMenuUI homeView;
	private ModelUI modelView;
	private ProcedureMenuUI procedureView;
	private PtuSelector ptuSelector;
	private MeasurementView measurementView;

	public APVSClientFactory() {
		// AtmosphereGWTSerializer serializer =
		// GWT.create(EventSerializer.class);
		// eventBus = new AtmosphereEventBus(serializer);
		remoteEventBus = new PollEventBus();
		placeController = new PlaceController(remoteEventBus);
		
		// specially for now in iPad app
		localEventBus = new RemoteEventBus();
	}

	@Override
	public RemoteEventBus getEventBus() {
		return remoteEventBus;
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
			homeView = new MainMenuList(this);
		}
		return homeView;
	}

	@Override
	public ModelUI getModelView() {
		if (modelView == null) {
			modelView = new ModelPanel(this);
		}
		return modelView;
	}

	@Override
	public PtuSelector getPtuSelector() {
//		if (ptuSelector == null) {
			ptuSelector = new PtuSelector(remoteEventBus, remoteEventBus);
//		}
		return ptuSelector;
	}

	@Override
	public MeasurementView getMeasurementView() {
//		if (measurementView == null) {
			measurementView = new MeasurementView(remoteEventBus, remoteEventBus);
//		}
		return measurementView;
	}

	@Override
	public CameraUI getCameraView(int type) {
		return new CameraPanel(remoteEventBus, remoteEventBus, type);
	}

	@Override
	public ProcedureMenuUI getProcedureMenuView() {
		if (procedureView == null) {
			procedureView = new ProcedureMenuPanel(this);
		}
		return procedureView;
	}
	
	@Override
	public ImageUI getImagePanel(String url) {
		return new ImagePanel(url);
	}

	@Override
	public ProcedureUI getProcedurePanel(String url, String name, String step) {
		return new ProcedurePanel(this, url, name, step);
	}
	
	@Override
	public ProcedureNavigator getProcedureNavigator() {
		return new ProcedureNavigator(localEventBus);
	}

	@Override
	public ProcedureView getProcedureView(int width, int height) {
		return new ProcedureView(remoteEventBus, localEventBus, width, height);
	}
}
