package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.user.cellview.client.Column;

public abstract class ClickableTextColumn<T> extends Column<T, String> {

	public ClickableTextColumn() {
		super(new ClickableTextCell());
	}
}
