package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class SupervisorWorkerView extends SplitLayoutPanel {

	public SupervisorWorkerView(RemoteEventBus eventBus) {
		addNorth(new ClientView(eventBus), 50);
		addWest(new MeasurementView(eventBus), 300);
		add(new ProcedureView(eventBus));
	}

}
