package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class EditableCell extends AbstractInputCell<String, Object> {
// FIXME add checkbox...
	private EditableTextCell textCell;
	private EditableSelectionCell selectionCell;
	private List<String> options;
	private Class<? extends Cell<String>>[] cellClass;
	
	public EditableCell(Class<? extends Cell<String>>[] cellClass) {
		this.cellClass = cellClass;
		options = new ArrayList<String>();
		options.add("option 1");
		options.add("option 2");
		options.add("option 3");
		options.add("option 4");
		options.add("option 5");
		options.add("option 6");
		selectionCell = new EditableSelectionCell(options);
		textCell = new EditableTextCell();
	}

	@Override
	public boolean isEditing(Context context, Element parent, String value) {
		if (getCellClass(context.getIndex()).equals(EditTextCell.class)) {
			return textCell.isEditing(context, parent, value);
		} else {
			return selectionCell.isEditing(context, parent, value);
		}
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, String value,
			NativeEvent event, ValueUpdater<String> valueUpdater) {
		if (getCellClass(context.getIndex()).equals(EditTextCell.class)) {
			textCell.onBrowserEvent(context, parent, value, event, valueUpdater);
		} else {
			selectionCell.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		if (getCellClass(context.getIndex()).equals(EditTextCell.class)) {
			textCell.render(context, value, sb);
		} else {
			selectionCell.render(context, value, sb);
		}
	}

	@Override
	public boolean dependsOnSelection() {
		return false;
	}

	@Override
	public Set<String> getConsumedEvents() {
		Set<String> events = new HashSet<String>();
		events.addAll(textCell.getConsumedEvents());
		events.addAll(selectionCell.getConsumedEvents());
		return events;
	}

	@Override
	public boolean handlesSelection() {
		return false;
	}
	
	@Override
	protected void onEnterKeyDown(Context context, Element parent,
			String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		if (getCellClass(context.getIndex()).equals(EditTextCell.class)) {
			textCell.onEnterKeyDown(context, parent, value, event, valueUpdater);
		} else {
			selectionCell.onEnterKeyDown(context, parent, value, event, valueUpdater);
		}
	}
	
	@Override
	public boolean resetFocus(Context context, Element parent, String value) {
		if (getCellClass(context.getIndex()).equals(EditTextCell.class)) {
			return textCell.resetFocus(context, parent, value);
		} else {
			return selectionCell.resetFocus(context, parent, value);
		}
	}

	@Override
	public void setValue(Context context, Element parent, String value) {
		if (getCellClass(context.getIndex()).equals(EditTextCell.class)) {
			textCell.setValue(context, parent, value);
		} else {
			selectionCell.setValue(context, parent, value);
		}
	}

	@Override
	public void clearViewData(Object key) {
		System.err.println("clearViewData should not be called");
	}

	@Override
	public Object getViewData(Object key) {
		System.err.println("getViewData should not be called");
		return null;
	}

	@Override
	public void setViewData(Object key, Object viewData) {
		System.err.println("setViewData should not be called");
	}

	@Override
	protected Element getInputElement(Element parent) {
		System.err.println("getInputElement should not be called");
		return null;
	}

	@Override
	protected void finishEditing(Element parent, String value, Object key,
			ValueUpdater<String> valueUpdater) {
		System.err.println("finishEditing should not be called");
	}

	private Class<? extends Cell<String>> getCellClass(int row) {
		if ((0 <= row) && (row < cellClass.length)) {
			return cellClass[row];
		}
		return EditTextCell.class;
	}
}
