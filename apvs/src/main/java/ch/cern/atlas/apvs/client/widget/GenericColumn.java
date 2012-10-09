package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;

public abstract class GenericColumn<T> extends Column<T, Object> implements DataStoreName {

	public GenericColumn(Cell<Object> cell) {
		super(cell);
	}

	// FIXME #88 can be removed when we go to gwt 2.5
	@Override
	public String getDataStoreName() {
		return null;
	}	
}
