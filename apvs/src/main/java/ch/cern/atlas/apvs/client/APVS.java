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
import ch.cern.atlas.apvs.client.ui.DosimeterView;
import ch.cern.atlas.apvs.client.ui.MeasurementView;
import ch.cern.atlas.apvs.client.ui.PlaceView;
import ch.cern.atlas.apvs.client.ui.ProcedureControls;
import ch.cern.atlas.apvs.client.ui.ProcedureView;
import ch.cern.atlas.apvs.client.ui.PtuView;
import ch.cern.atlas.apvs.client.ui.ServerSettingsView;
import ch.cern.atlas.apvs.client.ui.SupervisorSettingsView;
import ch.cern.atlas.apvs.client.ui.TimeView;
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
		
		boolean newCode = false;
		for(int i=0; i<divs.getLength(); i++) {
			String id = divs.getItem(i).getId();
			
			if (id.equals("Measurements")) {
				newCode = true;
				RootPanel.get("Measurements").add(new MeasurementView(clientFactory, workerEventBus));
			}
			
			if (id.equals("HelmetCamera")) {
				newCode = true;
				RootPanel.get("HelmetCamera").add(new CameraView(remoteEventBus, workerEventBus, CameraView.HELMET));
			}

			if (id.equals("HandCamera")) {
				newCode = true;
				RootPanel.get("HandCamera").add(new CameraView(remoteEventBus, workerEventBus, CameraView.HAND));
			}
			
			if (id.equals("Ptu")) {
				newCode = true;
				RootPanel.get("Ptu").add(new PtuView(clientFactory));				
			}
			
			if (id.equals("Procedure")) {
				newCode = true;
				RootPanel.get("Procedure").add(new ProcedureView(remoteEventBus, workerEventBus));				
			}
			
			if (id.equals("ProcedureControls")) {
				newCode = true;
				RootPanel.get("ProcedureControls").add(new ProcedureControls(workerEventBus));				
			}
			
			if (id.equals("Place")) {
				newCode = true;
				RootPanel.get("Place").add(new PlaceView(clientFactory, workerEventBus));				
			}
			
			if (id.equals("SupervisorSettings")) {
				newCode = true;
				RootPanel.get("SupervisorSettings").add(new SupervisorSettingsView(remoteEventBus));				
			}
			
			if (id.equals("ServerSettings")) {
				newCode = true;
				RootPanel.get("ServerSettings").add(new ServerSettingsView(remoteEventBus));				
			}
			
			if (id.equals("Dosimeter")) {
				newCode = true;
				RootPanel.get("Dosimeter").add(new DosimeterView(remoteEventBus));				
			}
			
			if (id.equals("Trace")) {
				newCode = true;
				RootPanel.get("Trace").add(new TimeView(clientFactory, 300, false));				
			}
		}
		
		// FIXME create tab buttons for each, select default one
		workerEventBus.fireEvent(new SelectPtuEvent(27372));
		
		if (newCode) return;


		startWorker();
		return;
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
