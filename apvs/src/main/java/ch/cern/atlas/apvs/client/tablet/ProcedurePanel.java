package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.ProcedureView;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

public class ProcedurePanel extends DetailPanel implements ProcedureUI {

	private LayoutPanel panel;
	private Button button;

	public ProcedurePanel(RemoteEventBus remoteEventBus,
			RemoteEventBus localEventBus, String url, String name, String step) {

		ProcedureView view = new ProcedureView(remoteEventBus, localEventBus, 800, 600);

		panel = new LayoutPanel();
		panel.add(view);

		if (MGWT.getOsDetection().isPhone()) {
			button = new Button("back");
			button.getElement().setAttribute("style",
					"margin:auto;width: 200px;display:block");
			panel.add(button);
			headerBackButton.removeFromParent();
		}

		scrollPanel.setWidget(panel);

		// width 100%
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
