package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;

public abstract class ClickableTextColumn<T> extends Column<T, String> {

	public ClickableTextColumn() {
		super(new ClickableTextCell());
	}
	
	public ClickableTextColumn(Cell<T> cell) {
		super(cell);
	}

	// FIXME #88 can be removed when we go to gwt 2.5
	public String getDataStoreName() {
		return null;
	}
}
