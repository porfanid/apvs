package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public abstract class ClickableHtmlColumn<T> extends ClickableTextColumn<T> {

	public ClickableHtmlColumn() {
	}
	
	public ClickableHtmlColumn(ActiveCell<String> cell) {
		super(cell);
	}
	
	@Override
	public void render(Context context, T object,
			SafeHtmlBuilder sb) {
		String value = getValue(object);
		if (value != null) {
			((ActiveClickableTextCell) getCell()).render(context,
					SafeHtmlUtils.fromSafeConstant(value), sb);
		}
	}

}
