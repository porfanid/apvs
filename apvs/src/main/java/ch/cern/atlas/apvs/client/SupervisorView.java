package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class SupervisorView extends DockPanel {

	public SupervisorView(RemoteEventBus eventBus) {
		add(new Label("Atlas Procedures Visualization System"), NORTH);
		add(new Label("Version 0.1"), SOUTH);
		
		VerticalFlowPanel west = new VerticalFlowPanel();
		add(west, WEST);
		west.add(new HTML("<b>Server Settings</b>"));
		west.add(new ServerSettingsView(eventBus));
		west.add(new HTML("<b>Settings</b>"));
		west.add(new SettingsView(eventBus));
		west.add(new HTML("<b>Dosimeters</b>"));
		west.add(new DosimeterView(eventBus));
		west.add(new HTML("<b>PTUs</b>"));
		west.add(new PtuView(eventBus));
				
		add(new HTML("<b>Workers</b>"), NORTH);
		add(new SupervisorWorkerView(eventBus), NORTH);
		add(new SupervisorWorkerView(eventBus), NORTH);
	}
}
