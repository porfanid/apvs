package ch.cern.atlas.apvs.client;

import java.util.List;

import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

public class EditableSelectionCell extends SelectionCell {

	public EditableSelectionCell(List<String> options) {
		super(options);
	}

	@Override
	protected void onEnterKeyDown(
			com.google.gwt.cell.client.Cell.Context context, Element parent,
			String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		super.onEnterKeyDown(context, parent, value, event, valueUpdater);
	}

	@Override
	protected Element getInputElement(Element parent) {
		return super.getInputElement(parent);
	}

	@Override
	protected void finishEditing(Element parent, String value, Object key,
			ValueUpdater<String> valueUpdater) {
		super.finishEditing(parent, value, key, valueUpdater);
	}
}
