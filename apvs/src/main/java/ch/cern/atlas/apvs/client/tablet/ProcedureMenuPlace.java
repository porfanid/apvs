package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ProcedureMenuPlace extends Place {
	public static class ProcedureMenuPlaceTokenizer implements
			PlaceTokenizer<ProcedureMenuPlace> {

		@Override
		public ProcedureMenuPlace getPlace(String token) {
			return new ProcedureMenuPlace();
		}

		@Override
		public String getToken(ProcedureMenuPlace place) {
			return "";
		}

	}
}
