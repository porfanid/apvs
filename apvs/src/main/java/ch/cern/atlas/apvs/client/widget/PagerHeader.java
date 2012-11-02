package ch.cern.atlas.apvs.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;

public class PagerHeader extends SimplePager {

	private Header<String> header;
	
	public PagerHeader() {

		final List<HasCell<String, ?>> cells = new ArrayList<HasCell<String, ?>>();
		cells.add(new HasCell<String, String>() {

			TextCell cell = new TextCell();

			@Override
			public Cell<String> getCell() {
				// TODO Auto-generated method stub
				return cell;
			}

			@Override
			public FieldUpdater<String, String> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getValue(String object) {
				// TODO Auto-generated method stub
				return "Label";
			}

		});
		cells.add(new HasCellImpl("Button1", new Delegate<String>() {
			@Override
			public void execute(String object) {
				Window.alert("B1 " + object);
			}
		}));
		cells.add(new HasCellImpl("Button2", new Delegate<String>() {
			@Override
			public void execute(String object) {
				Window.alert("B2 " + object);
			}
		}));
		header = new Header<String>(
				new CompositeCell<String>(cells)) {

			@Override
			public String getValue() {
				return "Composite";
			}
		};
	}

	public Header<String> getHeader() {
		return header;
	}

	private class HasCellImpl implements HasCell<String, String> {
		private ActionCell<String> cell;

		public HasCellImpl(String text, Delegate<String> delegate) {
			cell = new ActionCell<String>(text, delegate);
		}

		@Override
		public Cell<String> getCell() {
			return cell;
		}

		@Override
		public FieldUpdater<String, String> getFieldUpdater() {
			return null;
		}

		@Override
		public String getValue(String object) {
			return object;
		}
	}

}
