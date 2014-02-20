package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.user.cellview.client.Column;

public abstract class ActiveColumn<T, C> extends Column<T, C> {

	public ActiveColumn(ActiveCell<C> cell) {
		super(cell);
	}

	public boolean isEnabled() {
		return ((ActiveCell<C>)getCell()).isEnabled();
	}
	
	public void setEnabled(boolean enabled) {
		((ActiveCell<C>)getCell()).setEnabled(enabled);
	}
}
