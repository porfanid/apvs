package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Header;

public class ActionHeader extends Header<String> {

	private String text;
	private boolean visible;

	public ActionHeader(String text, Delegate<String> delegate) {
		super(new MyActionCell(text, delegate));
		this.text = text;
		this.visible = true;
	}

	@Override
	public String getValue() {
		return text;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		((MyActionCell)getCell()).setVisible(visible);
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public void render(Context context, SafeHtmlBuilder sb) {
		if (isVisible()) {
			super.render(context, sb);
		}
	}
	
	public boolean onPreviewColumnSortEvent(Context context, Element elem,
			NativeEvent event) {
		// events are handled, do not sort, fix for #454
		return false;
	}
	
	private static class MyActionCell extends ActionCell<String> {
		private boolean visible;

		public MyActionCell(String text, Delegate<String> delegate) {
			super(text, delegate);
			this.visible = true;
		}
		
		void setVisible(boolean visible) {
			this.visible = visible;
		}
				
		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				String value, SafeHtmlBuilder sb) {
			if (visible) {
				super.render(context, value, sb);
			}
		}
	}
}
