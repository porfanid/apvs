package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class ApvsTabletMainActivityMapper implements ActivityMapper {

	private final ClientFactory clientFactory;

	private Place lastPlace;

	public ApvsTabletMainActivityMapper(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;

	}

	@Override
	public Activity getActivity(Place place) {
		Activity activity = getActivity(lastPlace, place);
		lastPlace = place;
		return activity;

	}

	private AboutActivity aboutActivity;

	private AboutActivity getAboutActivity() {
		if (aboutActivity == null) {
			aboutActivity = new AboutActivity(clientFactory);
		}

		return aboutActivity;
	}

	private RadiationMapActivity radiationMapActivity;

	private RadiationMapActivity getRadiationMapActivity() {
		if (radiationMapActivity == null) {
			radiationMapActivity = new RadiationMapActivity(clientFactory);
		}

		return radiationMapActivity;
	}

	private ModelActivity modelActivity;

	private ModelActivity getModelActivity() {
		if (modelActivity == null) {
			modelActivity = new ModelActivity(clientFactory);
		}

		return modelActivity;
	}

	private Activity getActivity(Place lastPlace, Place newPlace) {
		if (newPlace instanceof HomePlace) {
			return getAboutActivity();
		}

		if (newPlace instanceof AboutPlace) {
			return getAboutActivity();
		}
		
		if (newPlace instanceof RadiationMapPlace) {
			return getRadiationMapActivity();
		}
		
		if (newPlace instanceof ModelPlace) {
			return getModelActivity();
		}
		
		System.err.println("Activity not handled "+newPlace);

		return null;
	}

}
