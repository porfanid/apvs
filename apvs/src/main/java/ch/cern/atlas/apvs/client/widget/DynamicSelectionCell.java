package ch.cern.atlas.apvs.client.widget;

import java.util.List;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

// Copied from SelectionCell and made options the "model" and removed indexOptions.
// Mark Donszelmann
public class DynamicSelectionCell extends AbstractInputCell<String, String> {

	interface Template extends SafeHtmlTemplates {
		@Template("<option value=\"{0}\">{0}</option>")
		SafeHtml deselected(String option);

		@Template("<option value=\"{0}\" selected=\"selected\">{0}</option>")
		SafeHtml selected(String option);
	}

	private static Template template;

	private List<String> options;

	/**
	 * Construct a new {@link SelectionCell} with the specified options.
	 * 
	 * @param options
	 *            the options in the cell
	 */
	public DynamicSelectionCell(List<String> options) {
		super("change");
		if (template == null) {
			template = GWT.create(Template.class);
		}
		this.options = options;
	}
	
	public DynamicSelectionCell() {
		this(null);
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, String value,
			NativeEvent event, ValueUpdater<String> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);

		if (options == null) {
			// Window.alert("Options are not set");
			return;
		}

		String type = event.getType();
		if ("change".equals(type)) {
			Object key = context.getKey();
			SelectElement select = parent.getFirstChild().cast();
			String newValue = options.get(select.getSelectedIndex());
			setViewData(key, newValue);
			finishEditing(parent, newValue, key, valueUpdater);
			if (valueUpdater != null) {
				valueUpdater.update(newValue);
			}
		}
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		if (options == null) {
			sb.appendHtmlConstant("<I>No Options Set</I>");
			return;
		}

		// Get the view data.
		Object key = context.getKey();
		String viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		int selectedIndex = options
				.indexOf(viewData == null ? value : viewData);
		sb.appendHtmlConstant("<select tabindex=\"-1\" class=\"dynamicSelectionCell\">");
		int index = 0;
		for (String option : options) {
			if (index++ == selectedIndex) {
				sb.append(template.selected(option));
			} else {
				sb.append(template.deselected(option));
			}
		}
		sb.appendHtmlConstant("</select>");
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}
}
