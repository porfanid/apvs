package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;

public class ClickableTextCell extends
		com.google.gwt.cell.client.ClickableTextCell {

	public ClickableTextCell() {
	}

	public ClickableTextCell(SafeHtmlRenderer<String> renderer) {
		super(renderer);
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			SafeHtml value, SafeHtmlBuilder sb) {
		super.render(context, value, sb);
	}
	
}
