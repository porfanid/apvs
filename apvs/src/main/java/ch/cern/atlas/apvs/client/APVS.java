package ch.cern.atlas.apvs.client;

import java.util.logging.Logger;

import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.service.ServerServiceAsync;
import ch.cern.atlas.apvs.client.settings.SettingsPersister;
import ch.cern.atlas.apvs.client.tablet.AppBundle;
import ch.cern.atlas.apvs.client.tablet.HomePlace;
import ch.cern.atlas.apvs.client.tablet.TabletHistoryObserver;
import ch.cern.atlas.apvs.client.tablet.TabletMenuActivityMapper;
import ch.cern.atlas.apvs.client.tablet.TabletMenuAnimationMapper;
import ch.cern.atlas.apvs.client.tablet.TabletPanelActivityMapper;
import ch.cern.atlas.apvs.client.tablet.TabletPanelAnimationMapper;
import ch.cern.atlas.apvs.client.tablet.TabletPlaceHistoryMapper;
import ch.cern.atlas.apvs.client.ui.CameraView;
import ch.cern.atlas.apvs.client.ui.ClientView;
import ch.cern.atlas.apvs.client.ui.DosimeterView;
import ch.cern.atlas.apvs.client.ui.MeasurementView;
import ch.cern.atlas.apvs.client.ui.PlaceView;
import ch.cern.atlas.apvs.client.ui.ProcedureControls;
import ch.cern.atlas.apvs.client.ui.ProcedureView;
import ch.cern.atlas.apvs.client.ui.PtuSelector;
import ch.cern.atlas.apvs.client.ui.PtuView;
import ch.cern.atlas.apvs.client.ui.ServerSettingsView;
import ch.cern.atlas.apvs.client.ui.SupervisorSettingsView;
import ch.cern.atlas.apvs.client.ui.SupervisorView;
import ch.cern.atlas.apvs.client.ui.TimeView;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.googlecode.mgwt.mvp.client.AnimatableDisplay;
import com.googlecode.mgwt.mvp.client.AnimatingActivityManager;
import com.googlecode.mgwt.mvp.client.AnimationMapper;
import com.googlecode.mgwt.mvp.client.history.MGWTPlaceHistoryHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;
import com.googlecode.mgwt.ui.client.dialog.TabletPortraitOverlay;
import com.googlecode.mgwt.ui.client.layout.MasterRegionHandler;
import com.googlecode.mgwt.ui.client.layout.OrientationRegionHandler;

/**
 * @author Mark Donszelmann
 */
public class APVS implements EntryPoint {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(getClass().getName());
	@SuppressWarnings("unused")
	private Window screen;

	private RemoteEventBus remoteEventBus;
	@SuppressWarnings("unused")
	private PlaceController placeController;
	@SuppressWarnings("unused")
	private SettingsPersister settingsPersister;

	@Override
	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(new APVSUncaughtExceptionHandler());

		Build build = GWT.create(Build.class);
		System.out.println("Starting APVS Version: " + build.version() + " - " + build.build()); 
		
		ServerServiceAsync.Util.getInstance().isReady(
				new AsyncCallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {
						if (result) {
							System.err.println("Server ready");
							start();
						} else {
							onFailure(null);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Server not ready. reload webpage "
								+ caught);
					}
				});
	}

	private void start() {
		
		ClientFactory clientFactory = GWT.create(ClientFactory.class);

		remoteEventBus = clientFactory.getEventBus();
		placeController = clientFactory.getPlaceController();

		// Turn off the browser scrollbars.
		Window.enableScrolling(false);

		settingsPersister = new SettingsPersister(remoteEventBus);

		// get first div element
		NodeList<Element> divs = Document.get().getElementsByTagName("div");
		if (divs.getLength() == 0) {
			Window.alert("Please define a <div> element with the id set to your view in the html you are starting from.");
			return;
		}

		// On a tab basis
		RemoteEventBus workerEventBus = new RemoteEventBus();
		
		// NEW CODE, (all ids without the View name)
		boolean newCode = false;
		for(int i=0; i<divs.getLength(); i++) {
			String id = divs.getItem(i).getId();
			
			if (id.equals("Measurements")) {
				newCode = true;
				RootPanel.get("Measurements").add(new MeasurementView(clientFactory, workerEventBus));
			}
			
			if (id.equals("HelmetCamera")) {
				newCode = true;
				RootPanel.get("HelmetCamera").add(new CameraView(remoteEventBus, workerEventBus, CameraView.HELMET, "100%", "100%"));
			}

			if (id.equals("HandCamera")) {
				newCode = true;
				RootPanel.get("HandCamera").add(new CameraView(remoteEventBus, workerEventBus, CameraView.HAND, "100%", "100%"));
			}

			if (id.equals("Worker")) {
				newCode = true;
				RootPanel.get("Worker").add(new PlaceView(clientFactory, workerEventBus, "100%", "100%"));
			}
			
			if (id.equals("Ptu")) {
				newCode = true;
				RootPanel.get("Ptu").add(new PtuView(clientFactory));				
			}
			
			if (id.equals("Procedure")) {
				newCode = true;
				RootPanel.get("Procedure").add(new ProcedureView(remoteEventBus, workerEventBus, "100%", "100%"));				
			}
			
			if (id.equals("ProcedureControls")) {
				newCode = true;
				RootPanel.get("ProcedureControls").add(new ProcedureControls(workerEventBus));				
			}
			
			if (id.equals("Place")) {
				newCode = true;
				RootPanel.get("Place").add(new PlaceView(clientFactory, workerEventBus, "100%", "100%"));				
			}
			
			if (id.equals("SupervisorSettings")) {
				newCode = true;
				RootPanel.get("SupervisorSettings").add(new SupervisorSettingsView(remoteEventBus));				
			}
			
			if (id.equals("ServerSettings")) {
				newCode = true;
				RootPanel.get("ServerSettings").add(new ServerSettingsView(remoteEventBus));				
			}
		}
		
		// FIXME create tab buttons for each, select default one
		workerEventBus.fireEvent(new SelectPtuEvent(27372));
		
		if (newCode) return;

		//** OLD CODE BELOW
		// Remove Defaults for Camera View and set to 100%

		String view = divs.getItem(0).getId();
		Panel p = new VerticalFlowPanel();
		if (view.equals("workerView")) {
			startWorker();
			return;

		} else if (view.equals("supervisorView")) {
			RootLayoutPanel.get().add(new SupervisorView(clientFactory));
			return;

		} else if (view.equals("clientView")) {
			p.add(new ClientView(remoteEventBus));

		} else if (view.equals("dosimeterView")) {
			p.add(new SupervisorSettingsView(remoteEventBus));
			p.add(new DosimeterView(remoteEventBus));

		} else if (view.equals("ptuView")) {
			p.add(new ServerSettingsView(remoteEventBus));
			p.add(new SupervisorSettingsView(remoteEventBus));
			p.add(new PtuView(clientFactory));

		} else if (view.equals("measurementView")) {
			RemoteEventBus localEventBus = new RemoteEventBus();
			p.add(new SupervisorSettingsView(remoteEventBus));
			p.add(new PtuSelector(remoteEventBus, localEventBus));
			p.add(new MeasurementView(clientFactory, localEventBus));

		} else if (view.equals("procedureView")) {
			RemoteEventBus localEventBus = new RemoteEventBus();
			p.add(new ProcedureView(remoteEventBus, localEventBus));
			p.add(new ProcedureControls(localEventBus));

		} else if (view.equals("placeView")) {
			RemoteEventBus localEventBus = new RemoteEventBus();
			p.add(new PtuSelector(remoteEventBus, localEventBus));
			p.add(new PlaceView(clientFactory, localEventBus));

		} else if (view.equals("traceView")) {
			p.add(new TimeView(clientFactory, 300, true));

		} else if (view.equals("settingsView")) {
			p.add(new SupervisorSettingsView(remoteEventBus));

		} else if (view.equals("cameraView")) {
			RemoteEventBus localEventBus = new RemoteEventBus();
			p.add(new ServerSettingsView(remoteEventBus));
			p.add(new SupervisorSettingsView(remoteEventBus));
			p.add(new PtuSelector(remoteEventBus, localEventBus));
			p.add(new CameraView(remoteEventBus, localEventBus,
					CameraView.HELMET));
			p.add(new CameraView(remoteEventBus, localEventBus, CameraView.HAND));

		}

		RootLayoutPanel.get().add(p);
	}

	private void startWorker() {

		// MGWTColorScheme.setBaseColor("#56a60D");
		// MGWTColorScheme.setFontColor("#eee");
		//
		// MGWTStyle.setDefaultBundle((MGWTClientBundle)
		// GWT.create(MGWTStandardBundle.class));
		// MGWTStyle.getDefaultClientBundle().getMainCss().ensureInjected();

		ViewPort viewPort = new MGWTSettings.ViewPort();
		viewPort.setTargetDensity(DENSITY.MEDIUM);
		viewPort.setUserScaleAble(false).setMinimumScale(1.0)
				.setMinimumScale(1.0).setMaximumScale(1.0);

		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewPort);
		// settings.setIconUrl("logo.png");
		// settings.setAddGlosToIcon(true);
		settings.setFullscreen(true);
		settings.setPreventScrolling(true);

		MGWT.applySettings(settings);

		final ClientFactory clientFactory = new APVSClientFactory();

		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		TabletPlaceHistoryMapper historyMapper = GWT
				.create(TabletPlaceHistoryMapper.class);

		if (MGWT.getOsDetection().isTablet()) {

			// very nasty workaround because GWT does not corretly support
			// @media
			StyleInjector.inject(AppBundle.INSTANCE.css().getText());

			createTabletDisplay(clientFactory);
		} else {

			createTabletDisplay(clientFactory);
			// createPhoneDisplay(clientFactory);

		}

		TabletHistoryObserver historyObserver = new TabletHistoryObserver();

		MGWTPlaceHistoryHandler historyHandler = new MGWTPlaceHistoryHandler(
				historyMapper, historyObserver);

		historyHandler.register(clientFactory.getPlaceController(),
				clientFactory.getEventBus(), new HomePlace());
		historyHandler.handleCurrentHistory();
	}

	/*
	 * private void createPhoneDisplay(ClientFactory clientFactory) {
	 * AnimatableDisplay display = GWT.create(AnimatableDisplay.class);
	 * 
	 * PhoneActivityMapper appActivityMapper = new PhoneActivityMapper(
	 * clientFactory);
	 * 
	 * PhoneAnimationMapper appAnimationMapper = new PhoneAnimationMapper();
	 * 
	 * AnimatingActivityManager activityManager = new AnimatingActivityManager(
	 * appActivityMapper, appAnimationMapper, clientFactory.getEventBus());
	 * 
	 * activityManager.setDisplay(display);
	 * 
	 * RootPanel.get().add(display);
	 * 
	 * }
	 */
	private void createTabletDisplay(ClientFactory clientFactory) {
		SimplePanel navContainer = new SimplePanel();
		navContainer.getElement().setId("nav");
		navContainer.getElement().addClassName("landscapeonly");
		AnimatableDisplay navDisplay = GWT.create(AnimatableDisplay.class);

		final TabletPortraitOverlay tabletPortraitOverlay = new TabletPortraitOverlay();

		new OrientationRegionHandler(navContainer, tabletPortraitOverlay,
				navDisplay);
		new MasterRegionHandler(clientFactory.getEventBus(), "nav",
				tabletPortraitOverlay);

		ActivityMapper navActivityMapper = new TabletMenuActivityMapper(
				clientFactory);

		AnimationMapper navAnimationMapper = new TabletMenuAnimationMapper();

		AnimatingActivityManager navActivityManager = new AnimatingActivityManager(
				navActivityMapper, navAnimationMapper,
				clientFactory.getEventBus());

		navActivityManager.setDisplay(navDisplay);

		RootPanel.get().add(navContainer);

		SimplePanel mainContainer = new SimplePanel();
		mainContainer.getElement().setId("main");
		AnimatableDisplay mainDisplay = GWT.create(AnimatableDisplay.class);

		TabletPanelActivityMapper tabletMainActivityMapper = new TabletPanelActivityMapper(
				clientFactory);

		AnimationMapper tabletMainAnimationMapper = new TabletPanelAnimationMapper();

		AnimatingActivityManager mainActivityManager = new AnimatingActivityManager(
				tabletMainActivityMapper, tabletMainAnimationMapper,
				clientFactory.getEventBus());

		mainActivityManager.setDisplay(mainDisplay);
		mainContainer.setWidget(mainDisplay);

		RootPanel.get().add(mainContainer);

	}
}
