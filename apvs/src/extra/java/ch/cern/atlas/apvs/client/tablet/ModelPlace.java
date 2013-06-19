package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.places.SharedPlace;

import com.google.gwt.place.shared.PlaceTokenizer;

public class ModelPlace extends SharedPlace {

	private static final long serialVersionUID = -5950820289563825202L;
	
	public ModelPlace() {
	}

	public static class ModelPlaceTokenizer implements
			PlaceTokenizer<ModelPlace> {

		@Override
		public ModelPlace getPlace(String token) {
			return new ModelPlace();
		}

		@Override
		public String getToken(ModelPlace place) {
			return "";
		}

	}
}
