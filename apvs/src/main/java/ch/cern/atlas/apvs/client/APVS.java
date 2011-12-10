package ch.cern.atlas.apvs.client;

import java.util.logging.Logger;

import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Mark Donszelmann
 */
public class APVS implements EntryPoint {

    private Logger logger = Logger.getLogger(getClass().getName());
	private Window screen;

	private RemoteEventBus remoteEventBus;
	private PlaceController placeController;
	@SuppressWarnings("unused")
	private SettingsPersister settingsPersister;

	@Override
	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(new APVSUncaughtExceptionHandler());

		ClientFactory clientFactory = GWT.create(ClientFactory.class);

		remoteEventBus = clientFactory.getEventBus();
		placeController = clientFactory.getPlaceController();

		// get first div element
		NodeList<Element> divs = Document.get().getElementsByTagName("div");
		if (divs.getLength() == 0) {
			Window.alert("Please define a <div> element with the id set to your view in the html you are starting from.");
			return;
		}
		
		settingsPersister = new SettingsPersister(remoteEventBus);

		String view = divs.getItem(0).getId();
		Panel p = new VerticalFlowPanel();
		if (view.equals("workerView")) {
			p.add(new WorkerView(remoteEventBus));
		} else if (view.equals("supervisorView")) {
			RootLayoutPanel.get().add(new SupervisorView(remoteEventBus));
			return;
		} else if (view.equals("clientView")) {
			p.add(new ClientView(remoteEventBus));
		} else if (view.equals("dosimeterView")) {
			p.add(new SettingsView(remoteEventBus));
			p.add(new DosimeterView(remoteEventBus));
		} else if (view.equals("ptuView")) {
			p.add(new SettingsView(remoteEventBus));			
			p.add(new PtuView(remoteEventBus));
		} else if (view.equals("measurementView")) {
			RemoteEventBus localEventBus = new RemoteEventBus();
			p.add(new SettingsView(remoteEventBus));
			p.add(new PtuSelector(remoteEventBus, localEventBus));
			p.add(new MeasurementView(remoteEventBus, localEventBus));
		} else if (view.equals("procedureView")) {
			ProcedureView procedureView = new ProcedureView(remoteEventBus);
			p.add(procedureView);
			p.add(new ProcedureControls(remoteEventBus));

			procedureView.setStep(1);
		} else if (view.equals("settingsView")) {
			p.add(new SettingsView(remoteEventBus));
		} else if (view.equals("cameraView")) {
			RemoteEventBus localEventBus = new RemoteEventBus();
			p.add(new SettingsView(remoteEventBus));
			p.add(new CameraView(remoteEventBus, localEventBus, CameraView.HELMET));
			p.add(new CameraView(remoteEventBus, localEventBus, CameraView.HAND));
		}
		
		RootLayoutPanel.get().add(p);
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

}
