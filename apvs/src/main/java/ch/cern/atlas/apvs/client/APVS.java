package ch.cern.atlas.apvs.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.gwt.client.AtmosphereClient;
import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.AtmosphereListener;
import org.atmosphere.gwt.client.extra.Window;

import ch.cern.atlas.apvs.client.places.Acquisition;
import ch.cern.atlas.apvs.client.places.Log;
import ch.cern.atlas.apvs.client.places.MenuPlace;
import ch.cern.atlas.apvs.client.places.Models;
import ch.cern.atlas.apvs.client.places.Procedures;
import ch.cern.atlas.apvs.client.places.RadiationMapping;
import ch.cern.atlas.apvs.client.places.RemotePlace;
import ch.cern.atlas.apvs.client.places.Settings;
import ch.cern.atlas.apvs.client.places.User;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author Mark Donszelmann
 */
public class APVS implements EntryPoint {
	
	DosimeterServiceAsync dosimeterService;
	PtuServiceAsync ptuService;
	AtmosphereClient client;
	Logger logger = Logger.getLogger(getClass().getName());
	Window screen;

	EventBus eventBus;
	PlaceController placeController;
	Label clientId = new Label();

	Button dosimeterButton;


	@Override
	public void onModuleLoad() {
		dosimeterService = GWT.create(DosimeterService.class);
		ptuService = GWT.create(PtuService.class);

		Panel p;
		p = RootPanel.get("dosimeterView");
		if (p != null) {
			p.add(new DosimeterView());
			return;
		}
		p = RootPanel.get("ptuView");
		if (p != null) {
			p.add(new PtuView());
			return;
		}
		p = RootPanel.get("measurementView");
		if (p != null) {
			DosimeterSelector dosimeterSelector = new DosimeterSelector();
			p.add(dosimeterSelector);
			p.add(new MeasurementView(dosimeterSelector));
			return;
		}
		p = RootPanel.get("measurementView");
		if (p != null) {
			p.add(new ProcedureView());
			return;
		}
		
		onMainModuleLoad();
	}

	private void onMainModuleLoad() {

		APVSCometListener cometListener = new APVSCometListener();

		AtmosphereGWTSerializer serializer = GWT.create(EventSerializer.class);
		// set a small length parameter to force refreshes
		// normally you should remove the length parameter
		client = new AtmosphereClient(GWT.getModuleBaseURL() + "apvsComet",
				serializer, cometListener);
		client.start();

		ClientFactory clientFactory = GWT.create(ClientFactory.class);

		eventBus = clientFactory.getEventBus();
		placeController = clientFactory.getPlaceController();

		eventBus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						RemotePlace place = (RemotePlace) event.getNewPlace();
						if (place.getRemoteID() == client.getConnectionID()) {
							GWT.log("Broadcast " + place);
							client.broadcast(place);
						} else {
							GWT.log("NoBroadcast");
						}
					}
				});

		RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();

		DockLayoutPanel panel = new DockLayoutPanel(Unit.EM);
		rootLayoutPanel.add(panel);

		panel.addWest(getLeftBar(), 20);
	}

	private Widget getLeftBar() {
		HorizontalPanel top = new HorizontalPanel();
		top.add(clientId);
		top.add(dosimeterButton);
		top.add(getUser());

		DockLayoutPanel panel = new DockLayoutPanel(Unit.EM);
		panel.addNorth(top, 2.0);
		panel.add(getStackedMenu());
		return panel;
	}

	private Widget getUser() {
		final ListBox comboBox = new ListBox();
		comboBox.addItem("Dimi");
		comboBox.addItem("Mark");
		comboBox.addItem("Marzio");
		comboBox.addItem("Olga");
		comboBox.addItem("Olivier");

		comboBox.setSelectedIndex(2);

		comboBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				GWT.log("New event");
				String user = comboBox.getValue(comboBox.getSelectedIndex());
				placeController.goTo(new User(client.getConnectionID(), user));
			}
		});

		eventBus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {

					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						Place place = event.getNewPlace();
						if (place instanceof User) {
							for (int i = 0; i < comboBox.getItemCount(); i++) {
								if (comboBox.getValue(i).equals(
										((User) place).getUser())) {
									comboBox.setSelectedIndex(i);
									return;
								}
							}
							GWT.log(place + " not found");
						}
					}
				});

		return comboBox;
	}

	private Widget getStackedMenu() {
		final List<MenuPlace> places = new ArrayList<MenuPlace>();
		places.add(new Settings());
		places.add(new Procedures());
		places.add(new Acquisition());
		places.add(new Models());
		places.add(new RadiationMapping());
		places.add(new Log());

		final StackLayoutPanel stackLayoutPanel = new StackLayoutPanel(Unit.EM);
		stackLayoutPanel.setPixelSize(200, 400);

		for (Iterator<MenuPlace> i = places.iterator(); i.hasNext();) {
			MenuPlace menuPlace = i.next();
			stackLayoutPanel.add(menuPlace.getWidget(), menuPlace.getHeader(),
					2.0);
		}

		stackLayoutPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				RemotePlace place = places.get(event.getSelectedItem());
				place.setRemoteID(client.getConnectionID());
				placeController.goTo(place);
			}
		});

		eventBus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {

					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						Place place = event.getNewPlace();
						if (place instanceof MenuPlace) {
							MenuPlace menuPlace = (MenuPlace) place;
							stackLayoutPanel.showWidget(menuPlace.getIndex(),
									false);
						}
					}
				});

		return stackLayoutPanel;
	}

	private class APVSCometListener implements AtmosphereListener {

		@Override
		public void onConnected(int heartbeat, int connectionID) {
			com.google.gwt.user.client.Window.alert("New connection "
					+ connectionID);
			GWT.log("comet.connected [" + heartbeat + ", " + connectionID + "]");
			clientId.setText(Integer.toString(connectionID));
		}

		@Override
		public void onBeforeDisconnected() {
			com.google.gwt.user.client.Window.alert("before disc "
					+ client.getConnectionID());
			logger.log(Level.INFO, "comet.beforeDisconnected");
		}

		@Override
		public void onDisconnected() {
			com.google.gwt.user.client.Window.alert("Disconnected "
					+ client.getConnectionID());
			GWT.log("comet.disconnected");
		}

		@Override
		public void onError(Throwable exception, boolean connected) {
			int statuscode = -1;
			if (exception instanceof StatusCodeException) {
				statuscode = ((StatusCodeException) exception).getStatusCode();
			}
			GWT.log("comet.error [connected=" + connected + "] (" + statuscode
					+ ")", exception);
			com.google.gwt.user.client.Window.alert("comet.error [connected="
					+ connected + "] (" + statuscode + ") " + exception);
			if (!connected) {
				clientId.setText(Integer.toString(0));
			}
		}

		@Override
		public void onHeartbeat() {
			GWT.log("comet.heartbeat [" + client.getConnectionID() + "]");
			com.google.gwt.user.client.Window.alert("comet.heartbeat ["
					+ client.getConnectionID() + "]");
		}

		@Override
		public void onRefresh() {
			GWT.log("comet.refresh [" + client.getConnectionID() + "]");
			com.google.gwt.user.client.Window.alert("comet.refresh ["
					+ client.getConnectionID() + "]");
		}

		@Override
		public void onMessage(List<? extends Serializable> messages) {
			com.google.gwt.user.client.Window.alert("comet.onMessage ["
					+ client.getConnectionID() + "]");
			StringBuilder result = new StringBuilder();
			for (Serializable obj : messages) {
				result.append(obj.toString()).append("<br/>");
			}
			logger.log(Level.INFO, "comet.message [" + client.getConnectionID()
					+ "] " + result.toString());
			Info.display("[" + client.getConnectionID() + "] Received "
					+ messages.size() + " messages", result.toString());
			for (Serializable msg : messages) {
				if (msg instanceof RemotePlace) {
					RemotePlace place = (RemotePlace) msg;
					if (place.getRemoteID() != client.getConnectionID()) {
						GWT.log("Incoming Broadcast "
								+ client.getConnectionID() + " -> " + place);
						eventBus.fireEvent(new PlaceChangeEvent(place));
					} else {
						GWT.log("Own Broadcast Ignored");
					}
				}
			}
		}
	}
}
