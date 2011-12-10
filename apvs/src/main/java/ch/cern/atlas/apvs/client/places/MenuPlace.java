package ch.cern.atlas.apvs.client.places;

import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("serial")
public abstract class MenuPlace extends RemotePlace {

	public abstract String getHeader();
	public abstract Widget getWidget();
	public abstract int getIndex();
}
