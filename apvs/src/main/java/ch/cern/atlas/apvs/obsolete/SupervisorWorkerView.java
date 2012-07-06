package ch.cern.atlas.apvs.obsolete;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.ui.CameraView;
import ch.cern.atlas.apvs.client.ui.MeasurementView;
import ch.cern.atlas.apvs.client.ui.PlaceView;
import ch.cern.atlas.apvs.client.ui.PtuSelector;
import ch.cern.atlas.apvs.client.widget.HorizontalFlowPanel;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;

public class SupervisorWorkerView extends DockPanel {
	
	private PtuSelector ptuSelector;
	private NameSelector nameSelector;
	
	public SupervisorWorkerView(ClientFactory clientFactory, Button deleteButton) {
		RemoteEventBus remoteEventBus = clientFactory.getEventBus();
		RemoteEventBus localEventBus = new RemoteEventBus();
		HorizontalFlowPanel p0 = new HorizontalFlowPanel();
		add(p0, NORTH);
		p0.add(new Label("PTU Id:"));
		ptuSelector = new PtuSelector(remoteEventBus, localEventBus);
		p0.add(ptuSelector);
		p0.add(new Label("Name:"));
		nameSelector = new NameSelector(remoteEventBus, localEventBus);
		p0.add(nameSelector);
//		p0.add(new Label("Worker Id:"));
//		p0.add(new WorkerId(remoteEventBus, localEventBus));
		deleteButton.setEnabled(false);
		p0.add(deleteButton);

		VerticalFlowPanel p4 = new VerticalFlowPanel();
		add(p4, EAST);
		p4.add(new Label("Measurements"));
		p4.add(new MeasurementView(clientFactory, localEventBus));

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
		p1.add(new Label("Worker's View"));
		p1.add(new PlaceView(clientFactory, localEventBus));
	}
	
	public Integer getPtuId() {
		return ptuSelector.getPtuId();
	}
	
	public String getName() {
		return nameSelector.getName();
	}	
}
