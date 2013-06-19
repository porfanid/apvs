package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.tablet.ModelEntrySelectedEvent.ModelEntry;

public class ModelItem {
	private String displayString;
	private final ModelEntry entry;

	public ModelItem(String displayString, ModelEntry entry) {
		this.displayString = displayString;
		this.entry = entry;

	}

	public String getDisplayString() {
		return displayString;
	}

	public ModelEntry getEntry() {
		return entry;
	}
}
