package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class TabletPanelActivityMapper implements ActivityMapper {

	private final ClientFactory clientFactory;

	private Place lastPlace;

	public TabletPanelActivityMapper(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;

	}

	@Override
	public Activity getActivity(Place place) {
		Activity activity = getActivity(lastPlace, place);
		lastPlace = place;
		return activity;

	}

	private ImageActivity defaultActivity;

	private ImageActivity getDefaultActivity() {
		if (defaultActivity == null) {
			defaultActivity = new ImageActivity(clientFactory,
					"ATLAS Procedures Visualization System",
					"Default-640x480.jpg");
		}

		return defaultActivity;
	}

	/* Main Panel */
	private Activity getActivity(Place lastPlace, Place newPlace) {
		if (newPlace instanceof HomePlace) {
			return getDefaultActivity();
		}
		
		if (newPlace instanceof ProcedureMenuPlace) {
			return getDefaultActivity();
		}
			
		if (newPlace instanceof CameraPlace) {
			return new CameraActivity(clientFactory);
		}
			
		if (newPlace instanceof ModelPlace) {
			return getDefaultActivity();
		}
		
		if (newPlace instanceof ProcedurePlace) {
			ProcedurePlace place = (ProcedurePlace)newPlace;
			return new ProcedureActivity(clientFactory, place.getUrl(), place.getName(), place.getStep());
		}
		
		if (newPlace instanceof ImagePlace) {
			ImagePlace place = (ImagePlace)newPlace;
			return new ImageActivity(clientFactory, place.getName(), place.getUrl());
		}
			
		System.err.println("Tablet Activity not handled "+newPlace);

		return null;
	}
}
