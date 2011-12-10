package ch.cern.atlas.apvs.client.places;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class Settings extends MenuPlace {

	private static final long serialVersionUID = -6211837841500847480L;

	public int getIndex() {
		return 0;
	}

	@Override
	public String getHeader() {
		return "Settings";
	}

	@Override
	public Widget getWidget() {
		return new HTML("TBD");
	}

}
