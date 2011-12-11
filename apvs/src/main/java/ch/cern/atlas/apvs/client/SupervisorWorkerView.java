package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;

public class SupervisorWorkerView extends DockPanel {

	public SupervisorWorkerView(RemoteEventBus remoteEventBus) {
		RemoteEventBus localEventBus = new RemoteEventBus();
		add(new PtuSelector(remoteEventBus, localEventBus), NORTH);
//		add(new MeasurementView(remoteEventBus, localEventBus), WEST);
		
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
		
		ProcedureView procedureView = new ProcedureView(remoteEventBus, localEventBus);
		p1.add(new Label("Procedure"));
		p1.add(procedureView);
		p1.add(new ProcedureControls(remoteEventBus, localEventBus));
		procedureView.setStep(12);
	}
}
