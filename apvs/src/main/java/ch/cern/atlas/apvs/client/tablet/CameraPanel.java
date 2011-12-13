package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

public class CameraPanel extends DetailPanel implements CameraUI {

	private TouchPanel panel;
	private Button button;

	public CameraPanel(RemoteEventBus remoteEventBus, RemoteEventBus localEventBus, int type) {

		panel = new TouchPanel();
		panel.add(new ch.cern.atlas.apvs.client.CameraView(remoteEventBus, localEventBus, type));

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
