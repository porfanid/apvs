package ch.cern.atlas.apvs.client.widget;

import java.util.ArrayList;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.user.cellview.client.Header;

public class CompositeHeader extends Header<String> {
	public CompositeHeader(Header<String>... headers) {
		super(new CompositeCell<String>(new CellList(headers)));
	}

	@Override
	public String getValue() {
		return "CompositeHeader";
	}
	
	private static class CellList extends ArrayList<HasCell<String, ?>> {
		private static final long serialVersionUID = 6750254855948081834L;

		public CellList(Header<String>... headers) {
			for (final Header<String> header: headers) {
				add(new HasCell<String, String>() {
					@Override
					public Cell<String> getCell() {
						return header.getCell();
					}

					@Override
					public FieldUpdater<String, String> getFieldUpdater() {
						return null;
					}

					@Override
					public String getValue(String object) {
						return null;
					}
				});
			}			
		}
	}
}
