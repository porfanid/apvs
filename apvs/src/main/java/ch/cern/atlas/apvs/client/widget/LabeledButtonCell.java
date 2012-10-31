package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class LabeledButtonCell extends ButtonCell implements Cell<String> {

	private String label;

	public LabeledButtonCell(String label) {
		this.label = label;
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		sb.appendHtmlConstant(label);
		if (value != null) {
			sb.appendHtmlConstant("&nbsp;");
			super.render(context, value, sb);
		}
	}
}
