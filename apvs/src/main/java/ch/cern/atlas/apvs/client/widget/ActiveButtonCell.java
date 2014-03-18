package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class ActiveButtonCell extends ButtonCell implements ActiveCell<String> {

	private boolean enabled = true;

	private static final SafeHtml BUTTON = SafeHtmlUtils
			.fromSafeConstant("<button type=\"button\" tabindex=\"-1\">");
	
	private static final SafeHtml BUTTON_DISABLED = SafeHtmlUtils
			.fromSafeConstant("<button type=\"button\" tabindex=\"-1\" disabled=\"disabled\">");

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
			Element parent, String value, NativeEvent event,
			ValueUpdater<String> valueUpdater) {
		if (enabled) {
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

	@Override
	public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
		sb.append(enabled ? BUTTON : BUTTON_DISABLED);
		if (data != null) {
			sb.append(data);
		}
		sb.appendHtmlConstant("</button>");
	}
}
