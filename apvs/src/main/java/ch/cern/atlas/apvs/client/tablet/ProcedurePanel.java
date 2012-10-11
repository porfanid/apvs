package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

public class ProcedurePanel extends DetailPanel implements ProcedureUI {

	private LayoutPanel panel;
	private Button button;

	public ProcedurePanel(ClientFactory clientFactory, String url, String name, String step) {
		this(clientFactory, url, name, step, 740 + Unit.PX.toString(),
				555 + Unit.PX.toString());
	}
		
	public ProcedurePanel(ClientFactory clientFactory, String url, String name, String step, String width, String height) {

		panel = new LayoutPanel();
		panel.add(clientFactory.getProcedureView(width, height, url, name, step));

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
		scrollPanel.setScrollingEnabledY(false);

		main.add(clientFactory.getProcedureNavigator());
	}

	@Override
	public HasTapHandlers getBackbutton() {
		if (MGWT.getOsDetection().isPhone()) {
			return button;
		}
		return super.getBackbutton();
	}

}
