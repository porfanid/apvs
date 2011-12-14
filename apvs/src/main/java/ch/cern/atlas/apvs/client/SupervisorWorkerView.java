package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.SelectStepEvent;
import ch.cern.atlas.apvs.client.widget.HorizontalFlowPanel;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;

public class SupervisorWorkerView extends DockPanel {

	public SupervisorWorkerView(RemoteEventBus remoteEventBus,
			Button deleteButton) {
		RemoteEventBus localEventBus = new RemoteEventBus();
		HorizontalFlowPanel p0 = new HorizontalFlowPanel();
		add(p0, NORTH);
		p0.add(new Label("PTU Id:"));
		p0.add(new PtuSelector(remoteEventBus, localEventBus));
		p0.add(new Label("Name:"));
		p0.add(new NameSelector(remoteEventBus, localEventBus));
		if (deleteButton != null) {
			p0.add(deleteButton);
		}

		VerticalFlowPanel p4 = new VerticalFlowPanel();
		add(p4, EAST);
		p4.add(new Label("Measurements"));
		p4.add(new MeasurementView(remoteEventBus, localEventBus));

		VerticalFlowPanel p3 = new VerticalFlowPanel();
		add(p3, EAST);
		p3.add(new Label("Hand Camera"));
		p3.add(new CameraView(remoteEventBus, localEventBus, CameraView.HAND));

		VerticalFlowPanel p2 = new VerticalFlowPanel();
		add(p2, EAST);
		p2.add(new Label("Helmet Camera"));
		p2.add(new CameraView(remoteEventBus, localEventBus, CameraView.HELMET));

		VerticalFlowPanel p1 = new VerticalFlowPanel();
		add(p1, EAST);

		ProcedureView procedureView = new ProcedureView(remoteEventBus,
				localEventBus);
		p1.add(new Label("Worker's View"));
		p1.add(procedureView);
		p1.add(new ProcedureControls(localEventBus));
		localEventBus.fireEvent(new SelectStepEvent(1));
	}
	
	public String getName() {
		return "FIXME";
	}
}
