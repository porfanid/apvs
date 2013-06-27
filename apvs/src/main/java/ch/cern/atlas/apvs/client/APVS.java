package ch.cern.atlas.apvs.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.domain.Ternary;
import ch.cern.atlas.apvs.client.event.ConnectionStatusChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.ConnectionStatusChangedRemoteEvent.ConnectionType;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.service.ServerService.User;
import ch.cern.atlas.apvs.client.settings.LocalStorage;
import ch.cern.atlas.apvs.client.settings.Proxy;
import ch.cern.atlas.apvs.client.settings.SettingsPersister;
import ch.cern.atlas.apvs.client.ui.AlarmView;
import ch.cern.atlas.apvs.client.ui.Arguments;
import ch.cern.atlas.apvs.client.ui.AudioSummary;
import ch.cern.atlas.apvs.client.ui.AudioSupervisorSettingsView;
import ch.cern.atlas.apvs.client.ui.AudioView;
import ch.cern.atlas.apvs.client.ui.CameraTable;
import ch.cern.atlas.apvs.client.ui.CameraView;
import ch.cern.atlas.apvs.client.ui.EventView;
import ch.cern.atlas.apvs.client.ui.GeneralInfoView;
import ch.cern.atlas.apvs.client.ui.InterventionView;
import ch.cern.atlas.apvs.client.ui.MeasurementTable;
import ch.cern.atlas.apvs.client.ui.MeasurementView;
import ch.cern.atlas.apvs.client.ui.Module;
import ch.cern.atlas.apvs.client.ui.PlaceView;
import ch.cern.atlas.apvs.client.ui.ProcedureControls;
import ch.cern.atlas.apvs.client.ui.ProcedureView;
import ch.cern.atlas.apvs.client.ui.PtuSettingsView;
import ch.cern.atlas.apvs.client.ui.PtuTabSelector;
import ch.cern.atlas.apvs.client.ui.PtuView;
import ch.cern.atlas.apvs.client.ui.ServerSettingsView;
import ch.cern.atlas.apvs.client.ui.Tab;
import ch.cern.atlas.apvs.client.ui.TimeView;
import ch.cern.atlas.apvs.client.widget.DialogResultEvent;
import ch.cern.atlas.apvs.client.widget.DialogResultHandler;
import ch.cern.atlas.apvs.client.widget.PasswordDialog;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Mark Donszelmann
 */
public class APVS implements EntryPoint {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	@SuppressWarnings("unused")
	private Window screen;

	private RemoteEventBus remoteEventBus;
	@SuppressWarnings("unused")
	private PlaceController placeController;
	@SuppressWarnings("unused")
	private SettingsPersister settingsPersister;

	private String defaultPtuId = "PTUdemo";
	
	private ClientFactory clientFactory;
	
	private Ternary alive = Ternary.Unknown;

	@Override
	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(new APVSUncaughtExceptionHandler());

		Build build = GWT.create(Build.class);
		log.info("Starting APVS Version: " + build.version() + " - "
				+ build.build());

		clientFactory = GWT.create(ClientFactory.class);
		
		clientFactory.getServerService().isReady(new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				checkSecure();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Server not ready. reload webpage "
						+ caught);
			}
		});
	}

	private void checkSecure() {
		clientFactory.getServerService().isSecure(new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean secure) {
				clientFactory.setSecure(secure);
				// FIXME, need a smarter proxy, uses full urls at this moment
				Proxy proxy = new Proxy(secure);		
//				proxy.put("/streams/1/helmet/", "http://pcatlaswpss02:8190/");
//				proxy.put("/streams/1/hand/", "http://pcatlaswpss02:8191/");
//				proxy.put("/APVS/", "http://localhost:8095/");
				proxy.put("http://atwss.cern.ch/streams/1/helmet/", "http://pcatlaswpss02:8190/");
				proxy.put("http://atwss.cern.ch/streams/1/hand/", "http://pcatlaswpss02:8191/");
				proxy.put("http://atwss.cern.ch/APVS/", "http://localhost:8095/");
				clientFactory.setProxy(proxy);

				if (secure) {
					login(null);
				} else {
					// not secure try with plain password
					String pwd = LocalStorage.getInstance()
							.get(LocalStorage.SUPERVISOR_PWD);
					if (pwd != null) {
						login(pwd);
					} else {
						prompt();
					}					
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// possibly secure, but do not check if supervisor
				clientFactory.setSecure(false);
				clientFactory.setProxy(new Proxy(false));
				start();
			}
		});
	}
	
	private void login(final String pwd) {
		clientFactory.getServerService().login(pwd,
				new AsyncCallback<User>() {

					@Override
					public void onSuccess(User user) {
						clientFactory.setUser(user);
						log.info("Server ready, user is "
								+ (user.isSupervisor() ? "SUPERVISOR" : "OBSERVER"));
						LocalStorage.getInstance().put(LocalStorage.SUPERVISOR_PWD, user.isSupervisor() ? pwd : null);
						start();
					}

					@Override
					public void onFailure(Throwable caught) {
						// ignore problem, start as an observer
						clientFactory.setUser(new User("Unknown", "", false));
						start();
					}
				});
	}

	private void prompt() {
		final PasswordDialog pwdDialog = new PasswordDialog();
		pwdDialog.addDialogResultHandler(new DialogResultHandler() {

			@Override
			public void onDialogResult(DialogResultEvent event) {
				login(event.getResult());
			}
		});
		pwdDialog.setModal(true);
		pwdDialog.setGlassEnabled(true);
		pwdDialog.setPopupPositionAndShow(new PositionCallback() {

			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				// center
				pwdDialog.setPopupPosition(
						(Window.getClientWidth() - offsetWidth) / 3,
						(Window.getClientHeight() - offsetHeight) / 3);
			}
		});

	}
	
	private void start() {
		
		remoteEventBus = clientFactory.getRemoteEventBus();
		placeController = clientFactory.getPlaceController();

		settingsPersister = new SettingsPersister(remoteEventBus);

		// get first div element
		NodeList<Element> divs = Document.get().getElementsByTagName("div");
		if (divs.getLength() == 0) {
			Window.alert("Please define a <div> element with the class set to your view you want to show.");
			return;
		}

		boolean layoutOnlyMode = Window.Location.getQueryString().indexOf(
				"layout=true") >= 0;
		if (layoutOnlyMode) {
			log.info("Running in layoutOnly mode");
			return;
		}

		for (int i = 0; i < divs.getLength(); i++) {
			Element element = divs.getItem(i);
			String id = element.getId();

			if (id.equals("footer")) {
				Label supervisor = new Label(
						clientFactory.getFullName()+" : "+
						(clientFactory.isSupervisor() ? "Supervisor"
								: "Observer"));
				supervisor.addStyleName("footer-left");
				RootPanel.get(id).insert(supervisor, 0);
				continue;
			}

			String[] parts = id.split("\\(", 2);
			if (parts.length == 2) {
				String className = parts[0];
				if ((parts[1].length() > 0) && !parts[1].endsWith(")")) {
					log.warn("Missing closing parenthesis on '" + id + "'");
					parts[1] += ")";
				}
				Arguments args = new Arguments(
						parts[1].length() > 0 ? parts[1].substring(0,
								parts[1].length() - 1) : null);

				log.info("Creating " + className + " with args (" + args + ")");

				Module module = null;
				// FIXME handle generically
				if (id.startsWith("MeasurementView")) {
					module = new MeasurementView();
				} else if (id.startsWith("MeasurementTable")) {
					module = new MeasurementTable();
				} else if (id.startsWith("AlarmView")) {
					module = new AlarmView();
				} else if (id.startsWith("AudioSummary")) {
					module = new AudioSummary();
				} else if (id.startsWith("AudioView")) {
					module = new AudioView();
				} else if (id.startsWith("AudioSupervisorSettingsView")) {
					module = new AudioSupervisorSettingsView();
				} else if (id.startsWith("CameraTable")) {
					module = new CameraTable();
				} else if (id.startsWith("CameraView")) {
					module = new CameraView();
				} else if (id.startsWith("EventView")) {
					module = new EventView();
				} else if (id.startsWith("GeneralInfoView")) {
					module = new GeneralInfoView();
				} else if (id.startsWith("InterventionView")) {
					module = new InterventionView();
				} else if (id.startsWith("PlaceView")) {
					module = new PlaceView();
				} else if (id.startsWith("ProcedureControls")) {
					module = new ProcedureControls();
				} else if (id.startsWith("ProcedureView")) {
					module = new ProcedureView();
				} else if (id.startsWith("PtuSettingsView")) {
					module = new PtuSettingsView();
				} else if (id.startsWith("PtuTabSelector")) {
					module = new PtuTabSelector();
				} else if (id.startsWith("PtuView")) {
					module = new PtuView();
				} else if (id.startsWith("ServerSettingsView")) {
					module = new ServerSettingsView();
				} else if (id.startsWith("Tab")) {
					module = new Tab();
				} else if (id.startsWith("TimeView")) {
					module = new TimeView();
				}

				if (module != null) {
					boolean add = module
							.configure(element, clientFactory, args);
					if (add && module instanceof IsWidget) {
						RootPanel.get(id).add((IsWidget) module);
					}
				}
			}
		}

		// FIXME create tab buttons for each, select default one
		clientFactory.getEventBus("ptu").fireEvent(
				new SelectPtuEvent(defaultPtuId));
		
		// Server ALIVE status
		RequestRemoteEvent.register(remoteEventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();

				if (type.equals(ConnectionStatusChangedRemoteEvent.class
						.getName())) {
					ConnectionStatusChangedRemoteEvent.fire(remoteEventBus,
							ConnectionType.server, alive);
				}
			}
		});


		Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				RequestBuilder request = PingServiceAsync.Util.getInstance().ping(new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						if (!alive.isTrue()) {
							alive = Ternary.True;
							ConnectionStatusChangedRemoteEvent.fire(remoteEventBus, ConnectionType.server, alive);
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						if (!alive.isFalse()) {
							alive = Ternary.False;
							ConnectionStatusChangedRemoteEvent.fire(remoteEventBus, ConnectionType.server, alive);							
						}
					}
				});
				
				request.setTimeoutMillis(10000);
				try {
					request.send();
				} catch (RequestException e) {
				}
				
				return true;
			}
		}, 20000);
		
		return;
	}
}
