package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;

public abstract class AsyncFieldUpdater<T, C> implements FieldUpdater<T, C> {

	private Context context;
	
	void setContext(Context context) {
		this.context = context;
	}
	
	protected Context getContext() {
		return context;
	}
	
}
