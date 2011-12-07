package ch.cern.atlas.apvs.client;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

public class EditableTextCell extends EditTextCell {

	public EditableTextCell() {
	}

	@Override
	protected void onEnterKeyDown(
			com.google.gwt.cell.client.Cell.Context context, Element parent,
			String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		// TODO Auto-generated method stub
		super.onEnterKeyDown(context, parent, value, event, valueUpdater);
	}
}
