package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.places.SharedPlace;

import com.google.gwt.place.shared.PlaceTokenizer;

public class ProcedureMenuPlace extends SharedPlace {

	private static final long serialVersionUID = 5771814036633594303L;
	
	public ProcedureMenuPlace() {
	}

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
