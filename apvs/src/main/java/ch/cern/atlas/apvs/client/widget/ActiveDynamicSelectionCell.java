package ch.cern.atlas.apvs.client.widget;

import java.util.List;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

public class ActiveDynamicSelectionCell extends DynamicSelectionCell implements
		ActiveCell<String> {
	
	private boolean enabled = true;
	
	public ActiveDynamicSelectionCell() {
	}
	
	public ActiveDynamicSelectionCell(List<String> options) {
		super(options);
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
			Element parent, String value, NativeEvent event,
			ValueUpdater<String> valueUpdater) {
		if (enabled) {
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}
}
