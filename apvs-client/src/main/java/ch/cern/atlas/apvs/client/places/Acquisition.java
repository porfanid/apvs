package ch.cern.atlas.apvs.client.places;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class Acquisition extends MenuPlace {

	private static final long serialVersionUID = -539192009894431782L;

	public int getIndex() {
		return 2;
	}

	@Override
	public Widget getHeader() {
		return new HTML("Acquisition");
	}

	@Override
	public Widget getWidget() {
		return new HTML("TBD");
	}

}
