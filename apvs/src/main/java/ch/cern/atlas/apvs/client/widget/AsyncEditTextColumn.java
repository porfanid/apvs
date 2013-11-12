package ch.cern.atlas.apvs.client.widget;

import ch.cern.atlas.apvs.domain.Device;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AsyncEditTextColumn<T> extends EditTextColumn<T>
		implements DataStoreName {

	public AsyncEditTextColumn() {
		this(new AsyncEditTextCell());
	}

	public AsyncEditTextColumn(Cell<String> cell) {
		super(cell);
	}

	@Override
	public void onBrowserEvent(Context context, Element elem, T object,
			NativeEvent event) {

		// set the context
		FieldUpdater<T, String> fieldUpdater = getFieldUpdater();
		if ((fieldUpdater != null)
				&& (fieldUpdater instanceof AsyncFieldUpdater<?, ?>)) {
			((AsyncFieldUpdater<T, String>) fieldUpdater).setContext(context);
		}

		super.onBrowserEvent(context, elem, object, event);
	}

	public AsyncCallback<Void> getCallback(final Context context,
			final Device device, final String value) {
		return new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				AsyncEditTextColumn.this.onSuccess(context, device, value,
						result);
			}

			@Override
			public void onFailure(Throwable caught) {
				AsyncEditTextColumn.this.onFailure(context, device, value,
						caught);
			}
		};
	}

	protected void onSuccess(Context context, Device device, String value,
			Void result) {
		((AsyncEditTextCell) getCell()).onSuccess(context, device, value,
				result);
	}

	protected void onFailure(Context context, Device device, String value,
			Throwable caught) {
		((AsyncEditTextCell) getCell()).onFailure(context, device, value,
				caught);
	}
}
