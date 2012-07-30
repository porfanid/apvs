package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.FileServiceAsync;
import ch.cern.atlas.apvs.client.service.PtuServiceAsync;
import ch.cern.atlas.apvs.client.tablet.CameraUI;
import ch.cern.atlas.apvs.client.tablet.ImageUI;
import ch.cern.atlas.apvs.client.tablet.MainMenuUI;
import ch.cern.atlas.apvs.client.tablet.ModelUI;
import ch.cern.atlas.apvs.client.tablet.ProcedureMenuUI;
import ch.cern.atlas.apvs.client.tablet.ProcedureNavigator;
import ch.cern.atlas.apvs.client.tablet.ProcedureUI;
import ch.cern.atlas.apvs.client.ui.MeasurementView;
import ch.cern.atlas.apvs.client.ui.ProcedureView;
import ch.cern.atlas.apvs.client.ui.PtuSelector;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {

	EventBus getEventBus(String name);
	RemoteEventBus getRemoteEventBus();

	PlaceController getPlaceController();

	FileServiceAsync getFileService();

	PtuServiceAsync getPtuService();

	MainMenuUI getHomeView();

	ModelUI getModelView();

	CameraUI getCameraView(String type);

	ProcedureMenuUI getProcedureMenuView();

	ImageUI getImagePanel(String url);

	ProcedureUI getProcedurePanel(String url, String name, String step);

	PtuSelector getPtuSelector();

	MeasurementView getMeasurementView();

	ProcedureNavigator getProcedureNavigator();

	ProcedureView getProcedureView(String width, String height);

	ProcedureView getProcedureView(String width, String height, String url, String name, String step);

}
