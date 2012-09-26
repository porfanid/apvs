package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;

public abstract class ClickableHtmlColumn<T> extends Column<T, String> {

	public ClickableHtmlColumn() {
		super(new ClickableTextCell());
	}
	
	@Override
	public void render(Context context, T object,
			SafeHtmlBuilder sb) {
		String value = getValue(object);
		if (value != null) {
			((ClickableTextCell) getCell()).render(context,
					SafeHtmlUtils.fromSafeConstant(value), sb);
		}
	}

}
