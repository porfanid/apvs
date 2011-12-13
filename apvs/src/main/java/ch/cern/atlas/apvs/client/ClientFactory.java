package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.FileServiceAsync;
import ch.cern.atlas.apvs.client.tablet.CameraUI;
import ch.cern.atlas.apvs.client.tablet.ImageUI;
import ch.cern.atlas.apvs.client.tablet.MainMenuUI;
import ch.cern.atlas.apvs.client.tablet.ModelUI;
import ch.cern.atlas.apvs.client.tablet.ProcedureMenuUI;
import ch.cern.atlas.apvs.client.tablet.ProcedureUI;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory {

	RemoteEventBus getEventBus();

	PlaceController getPlaceController();

	FileServiceAsync getFileService();

	MainMenuUI getHomeView();

	ModelUI getModelView();

	CameraUI getCameraView();

	ProcedureMenuUI getProcedureMenuView();

	ImageUI getImagePanel(String url);

	ProcedureUI getProcedurePanel(String url, String name, String step);
}
