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
	private Image image;
	private double imageRatio;

	// FIXME initial attachment seems correct, however animated attachment gives
	// 0 for width and height
	// we just use previous values.
	private static int width = 1;
	private static int height = 1;

	public ImagePanel(final String url) {

		image = new Image(url) {
			@Override
			protected void onAttach() {
				super.onAttach();
				imageRatio = (double) image.getWidth() / image.getHeight();
				resize();
			}
		};
		
		image.setWidth("100%");

		panel = new LayoutPanel();
		panel.add(image);

		if (MGWT.getOsDetection().isPhone()) {
			button = new Button("back");
			button.getElement().setAttribute("style",
					"margin:auto;width: 200px;display:block");
			panel.add(button);
			headerBackButton.removeFromParent();
		}

		main.add(panel);

		panel.addAttachHandler(new Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				resize();
			}
		});

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				resize();
			}
		});

		resize();
	}

	private void resize() {
		if (true) return;
		
		if (panel.getOffsetWidth() > 0) {
			width = panel.getOffsetWidth();
		}
		if (panel.getOffsetHeight() > 0) {
			height = panel.getOffsetHeight();
		}

		double panelRatio = (double) getWidth() / getHeight();

		if (imageRatio > panelRatio) {
			image.setWidth("100%");
			image.setHeight("");
		} else {
			image.setWidth("");
			image.setHeight("100%");
		}
	}

	@Override
	public HasTapHandlers getBackbutton() {
		if (MGWT.getOsDetection().isPhone()) {
			return button;
		}
		return super.getBackbutton();
	}

	private int getWidth() {
		return panel.getOffsetWidth() > 0 ? panel.getOffsetWidth() : width;
	}

	private int getHeight() {
		return panel.getOffsetHeight() > 0 ? panel.getOffsetHeight() : height;
	}
}
