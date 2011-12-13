package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ProcedurePlace extends Place {
	public static class ProcedurePlaceTokenizer implements
			PlaceTokenizer<ProcedurePlace> {

		@Override
		public ProcedurePlace getPlace(String token) {
			return new ProcedurePlace();
		}

		@Override
		public String getToken(ProcedurePlace place) {
			return "";
		}

	}
}
