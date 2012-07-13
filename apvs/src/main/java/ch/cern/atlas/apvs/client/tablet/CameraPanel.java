package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.ui.CameraView;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

public class CameraPanel extends DetailPanel implements CameraUI {

	private LayoutPanel panel;
	private Button button;

	public CameraPanel(RemoteEventBus remoteEventBus, RemoteEventBus localEventBus, String type) {

		panel = new LayoutPanel();
		panel.add(new CameraView(remoteEventBus, localEventBus, type, "800px", "600px"));

		if (MGWT.getOsDetection().isPhone()) {
			button = new Button("back");
			button.getElement().setAttribute("style",
					"margin:auto;width: 200px;display:block");
			panel.add(button);
			headerBackButton.removeFromParent();
		}

		scrollPanel.setWidget(panel);
		scrollPanel.setScrollingEnabledX(false);
		scrollPanel.setScrollingEnabledY(false);
	}

	@Override
	public HasTapHandlers getBackbutton() {
		if (MGWT.getOsDetection().isPhone()) {
			return button;
		}
		return super.getBackbutton();
	}

}
