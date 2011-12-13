package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ModelPlace extends Place {
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
