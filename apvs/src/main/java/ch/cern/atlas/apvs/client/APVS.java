package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import ch.cern.atlas.apvs.client.places.Acquisition;
import ch.cern.atlas.apvs.client.places.Log;
import ch.cern.atlas.apvs.client.places.MenuPlace;
import ch.cern.atlas.apvs.client.places.Models;
import ch.cern.atlas.apvs.client.places.Procedures;
import ch.cern.atlas.apvs.client.places.RadiationMapping;
import ch.cern.atlas.apvs.client.places.RemotePlace;
import ch.cern.atlas.apvs.client.places.Settings;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Mark Donszelmann
 */
public class APVS implements EntryPoint {

    private Logger logger = Logger.getLogger(getClass().getName());
	private Window screen;

	private RemoteEventBus eventBus;
	private PlaceController placeController;
	@SuppressWarnings("unused")
	private SettingsPersister settingsPersister;
	private Label clientId = new Label();

	private Button dosimeterButton;

	@Override
	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(new APVSUncaughtExceptionHandler());

		ClientFactory clientFactory = GWT.create(ClientFactory.class);

		eventBus = clientFactory.getEventBus();
		placeController = clientFactory.getPlaceController();

		// get first div element
		NodeList<Element> divs = Document.get().getElementsByTagName("div");
		if (divs.getLength() == 0) {
			Window.alert("Please define a <div> element with the id set to your view in the html you are starting from.");
			return;
		}
		
		settingsPersister = new SettingsPersister(eventBus);

		String view = divs.getItem(0).getId();
		Panel p = new VerticalFlowPanel();
		if (view.equals("workerView")) {
			p.add(new WorkerView(eventBus));
		} else if (view.equals("supervisorView")) {
			RootLayoutPanel.get().add(new SupervisorView(eventBus));
			return;
		} else if (view.equals("clientView")) {
			p.add(new ClientView(eventBus));
		} else if (view.equals("dosimeterView")) {
			p.add(new SettingsView(eventBus));
			p.add(new DosimeterView(eventBus));
		} else if (view.equals("ptuView")) {
			p.add(new SettingsView(eventBus));			
			p.add(new PtuView(eventBus));
		} else if (view.equals("measurementView")) {
			p.add(new DosimeterSelector(eventBus));
			p.add(new PtuSelector(eventBus));
			p.add(new MeasurementView(eventBus));
		} else if (view.equals("procedureView")) {
			ProcedureView procedureView = new ProcedureView(eventBus);
			p.add(procedureView);
			p.add(new ProcedureControls(eventBus));

			procedureView.setStep(1);
		} else if (view.equals("settingsView")) {
			p.add(new SettingsView(eventBus));
		} else if (view.equals("cameraView")) {
			RootLayoutPanel.get().add(new CameraView(eventBus));
			return;
		}
		
		RootLayoutPanel.get().add(p);
	}

	private void onMainModuleLoad() {
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
				// placeController.goTo(new User(client.getConnectionID(),
				// user));
			}
		});
		/*
		 * eventBus.addHandler(PlaceChangeEvent.TYPE, new
		 * PlaceChangeEvent.Handler() {
		 * 
		 * @Override public void onPlaceChange(PlaceChangeEvent event) { Place
		 * place = event.getNewPlace(); if (place instanceof User) { for (int i
		 * = 0; i < comboBox.getItemCount(); i++) { if
		 * (comboBox.getValue(i).equals( ((User) place).getUser())) {
		 * comboBox.setSelectedIndex(i); return; } } GWT.log(place +
		 * " not found"); } } });
		 */
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
				// place.setRemoteID(client.getConnectionID());
				placeController.goTo(place);
			}
		});
		/*
		 * eventBus.addHandler(PlaceChangeEvent.TYPE, new
		 * PlaceChangeEvent.Handler() {
		 * 
		 * @Override public void onPlaceChange(PlaceChangeEvent event) { Place
		 * place = event.getNewPlace(); if (place instanceof MenuPlace) {
		 * MenuPlace menuPlace = (MenuPlace) place;
		 * stackLayoutPanel.showWidget(menuPlace.getIndex(), false); } } });
		 */
		return stackLayoutPanel;
	}
}
