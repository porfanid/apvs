package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class TabletMenuActivityMapper implements ActivityMapper {

	private final ClientFactory clientFactory;

	public TabletMenuActivityMapper(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	private MainMenuActivity mainMenuActivity;

	private MainMenuActivity getMainMenuActivity() {
		if (mainMenuActivity == null) {
			mainMenuActivity = new MainMenuActivity(clientFactory);
		}
		return mainMenuActivity;
	}
	
	private ProcedureMenuActivity procedureActivity;

	private ProcedureMenuActivity getProcedureActivity() {
		if (procedureActivity == null) {
			procedureActivity = new ProcedureMenuActivity(clientFactory);
		}

		return procedureActivity;
	}

	private ModelActivity modelActivity;

	private ModelActivity getModelActivity() {
		if (modelActivity == null) {
			modelActivity = new ModelActivity(clientFactory);
		}

		return modelActivity;
	}

	/* Navigation Panel */
	@Override
	public Activity getActivity(Place place) {
		if (place instanceof ProcedureMenuPlace) {
			return getProcedureActivity();
		}

		if (place instanceof ProcedurePlace) {
			return getProcedureActivity();
		}

		if (place instanceof ModelPlace) {
			return getModelActivity();
		}

		if (place instanceof ImagePlace) {
			if (((ImagePlace)place).getName().equalsIgnoreCase("Radiation Map")) {
				return getMainMenuActivity();
			}
			if (((ImagePlace)place).getName().startsWith("ATLAS Procedures")) {
				return getMainMenuActivity();
			}
			return getModelActivity();
		}

		return getMainMenuActivity();
	}
}
