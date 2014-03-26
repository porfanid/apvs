package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class GlassPanel extends SimplePanel implements NativePreviewHandler {

	private boolean glassShowing;

	private static final String DEFAULT_STYLENAME = "glass-panel";

	public GlassPanel() {
		glassShowing = false;
		setWidth("100%");
	}

	public void add(Widget child) {
		super.add(child);
//		setCellWidth(child, "100%");
	}
	
	public void showGlass(boolean show) {
		this.glassShowing = show;
		if (show) {
			setStyleName(DEFAULT_STYLENAME);
		} else {
			setStyleName("");
		}
	}

	public boolean isGlassShowing() {
		return glassShowing;
	}

	// FIXME does not seem to prevent
	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		if (glassShowing) {
			event.consume();
			event.cancel();
		}
	}
}
