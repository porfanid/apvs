package ch.cern.atlas.apvs.client.places;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class Models extends MenuPlace {

	private static final long serialVersionUID = 4626220309430538489L;

	public int getIndex() {
		return 3;
	}

	@Override
	public String getHeader() {
		return "2D/3D Models";
	}

	@Override
	public Widget getWidget() {
		return new HTML("TBD");
	}

}
