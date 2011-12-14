package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

public class ImagePanel extends DetailPanel implements ImageUI {

	private LayoutPanel panel;
	private Button button;

	public ImagePanel(String url) {

		final Image image = new Image(url);
		image.setWidth("100%");
		// FIXME, make either width or height set to 100% depending on the ratio
		// of the image and the ratio of the panel.

		// image.setHeight("100%");

		panel = new LayoutPanel();
		panel.add(image);

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

		scrollPanel.addAttachHandler(new Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				System.err.println("A " + scrollPanel.getOffsetWidth() + " "
						+ scrollPanel.getOffsetHeight());
				System.err.println("P " + panel.getOffsetWidth() + " "
						+ panel.getOffsetHeight());
				System.err.println("I " + image.getWidth() + " "
						+ image.getHeight());
			}
		});

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				if (scrollPanel.getOffsetHeight() > 0) {
					System.err.println("A* "
							+ ((double)scrollPanel.getOffsetWidth() / scrollPanel
									.getOffsetHeight()));
				}
				if (image.getHeight() > 0) {
					System.err.println("I* "
							+ ((double)image.getWidth() / image.getHeight()));
				}
			}
		});
	}

	@Override
	public HasTapHandlers getBackbutton() {
		if (MGWT.getOsDetection().isPhone()) {
			return button;
		}
		return super.getBackbutton();
	}

}
