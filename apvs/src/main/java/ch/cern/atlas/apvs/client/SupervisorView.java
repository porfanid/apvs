package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;

public class SupervisorView extends DockPanel {

	public SupervisorView(RemoteEventBus eventBus) {
		add(new Label("Atlas Procedures Visualization System"), NORTH);
		add(new Label("Version 0.1"), SOUTH);
		
		TabPanel tabPanel = new TabPanel();
		add(tabPanel, NORTH);
		
		DockPanel mainPanel = new DockPanel();
		mainPanel.add(new SupervisorWorkerView(eventBus), NORTH);
		mainPanel.add(new SupervisorWorkerView(eventBus), NORTH);
		// FIXME add buttons
		tabPanel.add(mainPanel, "Workers");
		
		tabPanel.add(new PtuView(eventBus), "PTUs");
		tabPanel.add(new DosimeterView(eventBus), "Dosimeters");
		tabPanel.add(new SupervisorSettingsView(eventBus), "Supervisor Settings");
		tabPanel.add(new ServerSettingsView(eventBus), "Server Settings");
		
		tabPanel.selectTab(0);
	}
}
