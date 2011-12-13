package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.user.client.ui.Image;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

public class RadiationMapPanel extends DetailPanel implements
		RadiationMapUI {

	private TouchPanel panel;
	private Button button;

	public RadiationMapPanel() {

		panel = new TouchPanel();
		panel.add(new Image("images/InnerGapDosesMap.png"));

		if (MGWT.getOsDetection().isPhone()) {
			button = new Button("back");
			button.getElement().setAttribute("style",
					"margin:auto;width: 200px;display:block");
			panel.add(button);
			headerBackButton.removeFromParent();
		}

		scrollPanel.setWidget(panel);
//		scrollPanel.setScrollingEnabledX(false);
	}

	@Override
	public HasTapHandlers getBackbutton() {
		if (MGWT.getOsDetection().isPhone()) {
			return button;
		}
		return super.getBackbutton();
	}

}
