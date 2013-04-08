package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;

public class AudioActionCell implements HasCell<String, String> {
	    private ActionCell<String> cell;

	    public AudioActionCell(String text, Delegate<String> delegate) {
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
