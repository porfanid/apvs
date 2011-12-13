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
import ch.cern.atlas.apvs.client.tablet.ProcedurePanel;
import ch.cern.atlas.apvs.client.tablet.ProcedureUI;
import ch.cern.atlas.apvs.eventbus.client.PollEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;

public class APVSClientFactory implements ClientFactory {
	private RemoteEventBus eventBus;
	private final PlaceController placeController;
	private final FileServiceAsync fileService = GWT.create(FileService.class);

	private MainMenuUI homeView;
	private ModelUI modelView;
	private ProcedureMenuUI procedureView;

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
	public ModelUI getModelView() {
		if (modelView == null) {
			modelView = new ModelPanel();
		}
		return modelView;
	}

	@Override
	public CameraUI getCameraView() {
		return new CameraPanel(eventBus, eventBus, ch.cern.atlas.apvs.client.CameraView.HELMET);
	}

	@Override
	public ProcedureMenuUI getProcedureMenuView() {
		if (procedureView == null) {
			procedureView = new ProcedureMenuPanel();
		}
		return procedureView;
	}
	
	@Override
	public ImageUI getImagePanel(String url) {
		return new ImagePanel(url);
	}

	@Override
	public ProcedureUI getProcedurePanel(String url, String name, String step) {
		return new ProcedurePanel(eventBus, eventBus, url, name, step);
	}

}
