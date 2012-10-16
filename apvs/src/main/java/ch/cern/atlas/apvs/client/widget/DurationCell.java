package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

public class DurationCell extends AbstractCell<Long> {

	private final SafeHtmlRenderer<String> renderer;

	public DurationCell() {
		renderer = SimpleSafeHtmlRenderer.getInstance();
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			Long value, SafeHtmlBuilder sb) {
		if (value != null) {
			sb.append(renderer.render(HumanTime.upToMins(value)));
		}
	}
}
