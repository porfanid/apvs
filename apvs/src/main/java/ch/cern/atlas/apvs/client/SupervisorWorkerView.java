package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class SupervisorWorkerView extends SplitLayoutPanel {

	public SupervisorWorkerView(RemoteEventBus remoteEventBus) {
		RemoteEventBus localEventBus = new RemoteEventBus();
		addNorth(new PtuSelector(remoteEventBus, localEventBus), 50);
		addWest(new MeasurementView(remoteEventBus, localEventBus), 300);
		add(new ProcedureView(remoteEventBus));
	}

}
