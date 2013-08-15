package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.service.AudioServiceAsync;
import ch.cern.atlas.apvs.client.service.DbServiceAsync;
import ch.cern.atlas.apvs.client.service.EventServiceAsync;
import ch.cern.atlas.apvs.client.service.FileServiceAsync;
import ch.cern.atlas.apvs.client.service.InterventionServiceAsync;
import ch.cern.atlas.apvs.client.service.PtuServiceAsync;
import ch.cern.atlas.apvs.client.service.ServerService.User;
import ch.cern.atlas.apvs.client.service.ServerServiceAsync;
import ch.cern.atlas.apvs.client.settings.Proxy;
import ch.cern.atlas.apvs.client.ui.Arguments;
import ch.cern.atlas.apvs.client.ui.MeasurementView;
import ch.cern.atlas.apvs.client.ui.ProcedureView;
import ch.cern.atlas.apvs.eventbus.client.AtmosphereEventBus;
import ch.cern.atlas.apvs.eventbus.client.PollEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.web.bindery.event.shared.EventBus;

public class APVSClientFactory implements ClientFactory {

	@SuppressWarnings("unused")
	private AtmosphereEventBus atmosphereEventBus;
	private final PlaceController placeController;
	private final ServerServiceAsync serverService = ServerServiceAsync.Util.getInstance();
	private final FileServiceAsync fileService = FileServiceAsync.Util.getInstance();
	private final PtuServiceAsync ptuService = PtuServiceAsync.Util.getInstance();
	private final AudioServiceAsync audioService = AudioServiceAsync.Util.getInstance();
	private final DbServiceAsync dbService = DbServiceAsync.Util.getInstance();
	private final EventServiceAsync eventService = EventServiceAsync.Util.getInstance();
	private final InterventionServiceAsync interventionService = InterventionServiceAsync.Util.getInstance();

	private MeasurementView measurementView;
	private boolean secure;
	private User user;
	private Proxy proxy;
	
	public APVSClientFactory() {
		// atmosphereEventBus keeps track of connections, not used for actual polling of events
// FIXME #284, re-enable, but reload gives NPE onDisconnect in atmosphere-gwt
//		AtmosphereGWTSerializer serializer = GWT.create(EventSerializer.class);
//		atmosphereEventBus = new AtmosphereEventBus(serializer);

		AuthenticatingRequestBuilder requestBuilder = new AuthenticatingRequestBuilder();

		((ServiceDefTarget)serverService).setRpcRequestBuilder(requestBuilder);
		((ServiceDefTarget)fileService).setRpcRequestBuilder(requestBuilder);
		((ServiceDefTarget)ptuService).setRpcRequestBuilder(requestBuilder);
		((ServiceDefTarget)audioService).setRpcRequestBuilder(requestBuilder);
		((ServiceDefTarget)dbService).setRpcRequestBuilder(requestBuilder);
		((ServiceDefTarget)eventService).setRpcRequestBuilder(requestBuilder);
		((ServiceDefTarget)interventionService).setRpcRequestBuilder(requestBuilder);

		// used for events
		RemoteEventBus remoteEventBus = new PollEventBus(requestBuilder);
		NamedEventBus.put("remote", remoteEventBus);
		placeController = new PlaceController(remoteEventBus);	
		
		secure = false;		
	}

	@Override
	public EventBus getEventBus(String name) {
		return NamedEventBus.get(name);
	}

	@Override
	public RemoteEventBus getRemoteEventBus() {
		return (RemoteEventBus)NamedEventBus.get("remote");
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
	public PtuServiceAsync getPtuService() {
		return ptuService;
	}
	
	@Override
	public MeasurementView getMeasurementView() {
//		if (measurementView == null) {
			measurementView = new MeasurementView();
			measurementView.configure(null, this, new Arguments());
//		}
		return measurementView;
	}

	@Override
	public ProcedureView getProcedureView(String width, String height) {
		// FIXME #178 width and height ignored
		ProcedureView v = new ProcedureView();
		v.configure(null, this, new Arguments());
		return v;
	}

	@Override
	public ProcedureView getProcedureView(
			String width, String height, String url, String name, String step) {
		// FIXME #178 width and height and name and step ignored
		ProcedureView v = new ProcedureView();
		v.configure(null, this, new Arguments());
		return v;
	}

	@Override
	public ServerServiceAsync getServerService() {
		return serverService;
	}

	@Override
	public AudioServiceAsync getAudioService() {
		return audioService;
	}

	@Override
	public DbServiceAsync getDbService() {
		return dbService;
	}

	@Override
	public EventServiceAsync getEventService() {
		return eventService;
	}

	@Override
	public InterventionServiceAsync getInterventionService() {
		return interventionService;
	}
	
	@Override
	public void setSecure(boolean secure) {
		this.secure = secure;		
	}
	
	@Override
	public boolean isSecure() {
		return secure;
	}
	
	@Override
	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
	public boolean isSupervisor() {
		return user.isSupervisor();
	}
	
	@Override
	public String getFullName() {
		return user.getFullName();
	}
	
	@Override
	public String getEmail() {
		return user.getEmail();
	}

	@Override
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}
	
	@Override
	public Proxy getProxy() {
		return proxy;
	}
}