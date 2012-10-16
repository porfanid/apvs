package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.user.client.ui.IsWidget;

public interface IsSwitchableWidget extends IsWidget {
	
	public boolean isDestination();

	public void toggleDestination();
}
