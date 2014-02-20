package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;

public class ActiveClickableTextCell extends ClickableTextCell implements
		ActiveCell<String> {

	private boolean enabled = true;

	public ActiveClickableTextCell() {
	}

	public ActiveClickableTextCell(SafeHtmlRenderer<String> renderer) {
		super(renderer);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			SafeHtml value, SafeHtmlBuilder sb) {
		super.render(context, value, sb);
	}

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
			Element parent, String value, NativeEvent event,
			ValueUpdater<String> valueUpdater) {
		if (enabled) {
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}
}
