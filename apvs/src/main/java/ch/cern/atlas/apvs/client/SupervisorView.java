package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SupervisorView extends SplitLayoutPanel {

	public SupervisorView(RemoteEventBus eventBus) {
		VerticalFlowPanel west = new VerticalFlowPanel();
		addWest(west, 600);
		west.add(new HTML("<b>Dosimeters</b>"));
		west.add(new DosimeterView(eventBus));
		west.add(new HTML("<b>PTUs</b>"));
		west.add(new PtuView(eventBus));
		
		ScrollPanel center = new ScrollPanel();
		add(center);

		VerticalFlowPanel vertical = new VerticalFlowPanel();
		center.add(vertical);
		
		vertical.add(new HTML("<b>Workers</b>"));
		vertical.add(new SupervisorWorkerView(eventBus));
		vertical.add(new SupervisorWorkerView(eventBus));
	}

}
