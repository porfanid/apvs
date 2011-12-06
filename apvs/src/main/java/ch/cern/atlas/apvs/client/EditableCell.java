package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class EditableCell extends AbstractInputCell<String, Object> {

	private EditTextCell textCell;
	private SelectionCell selectionCell;
	private List<String> options;
	private Class<? extends Cell<String>>[] cellClass;
	
	public EditableCell(Class<? extends Cell<String>>[] cellClass) {
		this.cellClass = cellClass;
		options = new ArrayList<String>();
		selectionCell = new SelectionCell(options);
		textCell = new EditTextCell();
	}

	@Override
	public boolean isEditing(Context context, Element parent, String value) {
		System.err.println("isEditing");
		return textCell.isEditing(context, parent, value);
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, String value,
			NativeEvent event, ValueUpdater<String> valueUpdater) {
		System.err.println("onBrowserEvent");
		textCell.onBrowserEvent(context, parent, value, event, valueUpdater);
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
	public void clearViewData(Object key) {
		System.err.println("clearViewData");
		textCell.clearViewData(key);
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
	public Object getViewData(Object key) {
		System.err.println("getViewData");
		return textCell.getViewData(key);
	}

	@Override
	public boolean handlesSelection() {
		return false;
	}

	@Override
	protected void onEnterKeyDown(Context context, Element parent,
			String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		System.err.println("onEnterKeyDown");
		// cell.onEnterKeyDown(context, parent, value, event, valueUpdater);
	}

	@Override
	public boolean resetFocus(Context context, Element parent, String value) {
		System.err.println("resetFocus");
		return textCell.resetFocus(context, parent, value);
	}

	@Override
	public void setValue(Context context, Element parent, String value) {
		System.err.println("setValue");
		textCell.setValue(context, parent, value);
	}

	@Override
	public void setViewData(Object key, Object viewData) {
		System.err.println("setViewData");
		// cell.setViewData(key, viewData);
	}

	@Override
	protected Element getInputElement(Element parent) {
		System.err.println("getInputElement");
		// return cell.getInputElement(parent);
		return null;
	}

	@Override
	protected void finishEditing(Element parent, String value, Object key,
			ValueUpdater<String> valueUpdater) {
		System.err.println("finishEditing");
		// cell.finishEditing(parent, value, key, valueUpdater);
	}

	private Class<? extends Cell<String>> getCellClass(int row) {
		if ((0 <= row) && (row < cellClass.length)) {
			return cellClass[row];
		}
		return EditTextCell.class;
	}
}
