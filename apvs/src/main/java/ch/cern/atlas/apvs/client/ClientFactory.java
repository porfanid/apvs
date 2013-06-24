package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.AudioServiceAsync;
import ch.cern.atlas.apvs.client.service.DbServiceAsync;
import ch.cern.atlas.apvs.client.service.EventServiceAsync;
import ch.cern.atlas.apvs.client.service.FileServiceAsync;
import ch.cern.atlas.apvs.client.service.InterventionServiceAsync;
import ch.cern.atlas.apvs.client.service.PtuServiceAsync;
import ch.cern.atlas.apvs.client.service.ServerService.User;
import ch.cern.atlas.apvs.client.service.ServerServiceAsync;
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

	ServerServiceAsync getServerService();
	FileServiceAsync getFileService();
	PtuServiceAsync getPtuService();
	AudioServiceAsync getAudioService();
	DbServiceAsync getDbService();
	EventServiceAsync getEventService();
	InterventionServiceAsync getInterventionService();

	PtuSelector getPtuSelector();

	MeasurementView getMeasurementView();

	ProcedureView getProcedureView(String width, String height);

	ProcedureView getProcedureView(String width, String height, String url, String name, String step);
	
	void setUser(User user);
	boolean isSupervisor();
	String getFullName();
	String getEmail();
}
