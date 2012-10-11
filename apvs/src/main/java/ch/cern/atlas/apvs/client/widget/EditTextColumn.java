package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;

public abstract class EditTextColumn<T> extends Column<T, String> implements DataStoreName {

	public EditTextColumn() {
		super(new EditTextCell());
	}
		
	public EditTextColumn(Cell<String> cell) {
		super(cell);
	}
	
	@Override
	public void render(Context context, T object, SafeHtmlBuilder sb) {
		// TODO Auto-generated method stub
		super.render(context, object, sb);
	}

	// FIXME #88 can be removed when we go to gwt 2.5
	@Override
	public String getDataStoreName() {
		return null;
	}
}
