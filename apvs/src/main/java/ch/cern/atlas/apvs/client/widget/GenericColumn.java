package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;

public abstract class GenericColumn<T> extends Column<T, Object> {

	public GenericColumn(Cell<Object> cell) {
		super(cell);
	}
}
