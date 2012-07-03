package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.DockPanel;

public class ProcedurePanel extends DockPanel {
	public ProcedurePanel(RemoteEventBus remoteEventBus, String width, String height) {
		RemoteEventBus localEventBus = new RemoteEventBus();
		
		// FIXME add buttons to select procedure
		add(new ProcedureView(remoteEventBus, localEventBus, width, height), NORTH);
		add(new ProcedureControls(localEventBus), SOUTH);
	}
}
