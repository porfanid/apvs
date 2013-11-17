package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;

public abstract class CheckboxColumn<T> extends Column<T, Boolean> {

	private boolean enabled;
	
	public CheckboxColumn() {
		super(new ActiveCheckboxCell());
		enabled = true;
	}
		
	public CheckboxColumn(ActiveCheckboxCell cell) {
		super(cell);
	}
	
	@Override
	public void render(Context context, T object, SafeHtmlBuilder sb) {
		super.render(context, object, sb);
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
		((ActiveCheckboxCell)getCell()).setEnabled(enabled);
	}
	
	public boolean isEnabled() {
		return enabled;
	}	
}
