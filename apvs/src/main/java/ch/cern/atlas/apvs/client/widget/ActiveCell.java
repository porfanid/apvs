package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.Cell;

public interface ActiveCell<C> extends Cell<C> {

	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
}
