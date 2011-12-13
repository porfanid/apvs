package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.tablet.ProcedureEntrySelectedEvent.ProcedureEntry;

public class ProcedureItem {
	private String displayString;
	private final ProcedureEntry entry;

	public ProcedureItem(String displayString, ProcedureEntry entry) {
		this.displayString = displayString;
		this.entry = entry;

	}

	public String getDisplayString() {
		return displayString;
	}

	public ProcedureEntry getEntry() {
		return entry;
	}
}
