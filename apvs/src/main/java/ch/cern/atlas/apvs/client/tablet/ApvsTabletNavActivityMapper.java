package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class ApvsTabletNavActivityMapper implements ActivityMapper {

	private final ClientFactory clientFactory;

	public ApvsTabletNavActivityMapper(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	private ShowCaseListActivity showCaseListActivity;

	private Activity getShowCaseListActivity() {
		if (showCaseListActivity == null) {
			showCaseListActivity = new ShowCaseListActivity(clientFactory);
		}
		return showCaseListActivity;
	}

	@Override
	public Activity getActivity(Place place) {
		if (place instanceof HomePlace) {
			return getShowCaseListActivity();
		}

		return new ShowCaseListActivity(clientFactory);
	}
}
