package ch.cern.atlas.apvs.client.widget;

import java.util.ArrayList;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.user.cellview.client.Column;

/**
 * NOTE: NOT TESTED
 * @author duns
 *
 */
public class CompositeColumn extends Column<String, String> {
	public CompositeColumn(Column<String,String>... columns) {
		super(new CompositeCell<String>(new CellList(columns)));
	}

	@Override
	public String getValue(String object) {
		return object;
	}
	
	private static class CellList extends ArrayList<HasCell<String, ?>> {

		private static final long serialVersionUID = 8188871056982325315L;

		public <T> CellList(Column<T, String>... columns) {
			for (final Column<T, String> column: columns) {
				add(new HasCell<String, String>() {
					@Override
					public Cell<String> getCell() {
						return column.getCell();
					}

					@Override
					public FieldUpdater<String, String> getFieldUpdater() {
						return null;
					}

					@Override
					public String getValue(String object) {
						return "Cell"+object;
					}
				});
			}			
		}
	}
}
