package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.user.client.ui.Image;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

public class AboutPanel extends DetailPanel implements AboutUI {

	private LayoutPanel panel;
	private Button button;

	public AboutPanel() {

		panel = new LayoutPanel();

		panel.add(new Image("Default-640x480.jpg"));

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
