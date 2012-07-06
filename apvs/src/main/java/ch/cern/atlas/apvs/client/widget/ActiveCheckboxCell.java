package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class ActiveCheckboxCell extends com.google.gwt.cell.client.CheckboxCell {

	private static final SafeHtml INPUT_CHECKED = SafeHtmlUtils
			.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked/>");

	private static final SafeHtml INPUT_UNCHECKED = SafeHtmlUtils
			.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\"/>");

	private static final SafeHtml INPUT_CHECKED_DISABLED = SafeHtmlUtils
			.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked disabled=\"disabled\"/>");

	private static final SafeHtml INPUT_UNCHECKED_DISABLED = SafeHtmlUtils
			.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" disabled=\"disabled\"/>");

	@Override
	public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
		// Get the view data.
		Object key = context.getKey();
		Boolean viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		Boolean checked = viewData != null ? viewData : value;
		// FIXME make this set-able
		boolean enabled = false;

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
}
