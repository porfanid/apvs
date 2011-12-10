package ch.cern.atlas.apvs.client.places;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class RadiationMapping extends MenuPlace {

	private static final long serialVersionUID = -4356287206313986067L;

	public int getIndex() {
		return 4;
	}

	@Override
	public String getHeader() {
		return "Radiation Mapping";
	}

	@Override
	public Widget getWidget() {
		return new HTML("TBD");
	}

}
