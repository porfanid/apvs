package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.user.client.ui.HTML;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

public class AboutViewGwtImpl extends DetailViewGwtImpl implements AboutView {

	private RoundPanel round;
	private Button button;

	public AboutViewGwtImpl() {

		round = new RoundPanel();

		round.add(new HTML("APVS"));
		round.add(new HTML("ATLAS Procedures Visualization System"));
		round.add(new HTML("Version 0.1"));

		if (MGWT.getOsDetection().isPhone()) {
			button = new Button("back");
			button.getElement().setAttribute("style",
					"margin:auto;width: 200px;display:block");
			round.add(button);
			headerBackButton.removeFromParent();
		}

		scrollPanel.setWidget(round);
		scrollPanel.setScrollingEnabledX(false);
	}

	@Override
	public HasTapHandlers getBackbutton() {
		if (MGWT.getOsDetection().isPhone()) {
			return button;
		}
		return super.getBackbutton();
	}

}
