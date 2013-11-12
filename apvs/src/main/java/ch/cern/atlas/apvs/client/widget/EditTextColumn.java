package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.cellview.client.Column;

public abstract class EditTextColumn<T> extends Column<T, String> implements DataStoreName {

	private boolean enabled;
	
	public EditTextColumn() {
		this(new EditTextCell());
	}
		
	public EditTextColumn(Cell<String> cell) {
		super(cell);
		enabled = true;
	}
		
	@Override
	public void onBrowserEvent(Context context, Element elem, T object,
			NativeEvent event) {
		if (enabled) {
			super.onBrowserEvent(context, elem, object, event);
		}
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public String getDataStoreName() {
		return null;
	}
}
