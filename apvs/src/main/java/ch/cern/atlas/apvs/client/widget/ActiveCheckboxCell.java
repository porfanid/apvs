package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

public class ActiveCheckboxCell extends CheckboxCell implements ActiveCell<Boolean> {

	private static final SafeHtml INPUT_CHECKED = SafeHtmlUtils
			.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked/>");

	private static final SafeHtml INPUT_UNCHECKED = SafeHtmlUtils
			.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\"/>");

	private static final SafeHtml INPUT_CHECKED_DISABLED = SafeHtmlUtils
			.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked disabled=\"disabled\"/>");

	private static final SafeHtml INPUT_UNCHECKED_DISABLED = SafeHtmlUtils
			.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" disabled=\"disabled\"/>");

	private boolean enabled = true;
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
		// Get the view data.
		Object key = context.getKey();
		Boolean viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		boolean checked = (value != null) && (viewData != null ? viewData : value);

		if (checked && !enabled) {
			sb.append(INPUT_CHECKED_DISABLED);
		} else if (!checked && !enabled) {
			sb.append(INPUT_UNCHECKED_DISABLED);
		} else if (checked && enabled) {
			sb.append(INPUT_CHECKED);
		} else if (!checked && enabled) {
			sb.append(INPUT_UNCHECKED);
		}
	}
	
	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
			Element parent, Boolean value, NativeEvent event,
			ValueUpdater<Boolean> valueUpdater) {
		if (enabled) {
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}
}
