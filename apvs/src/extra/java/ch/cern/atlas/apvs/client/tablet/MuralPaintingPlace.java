package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class MuralPaintingPlace extends Place {
	public static class MuralPaintingPlaceTokenizer implements
			PlaceTokenizer<MuralPaintingPlace> {

		@Override
		public MuralPaintingPlace getPlace(String token) {
			return new MuralPaintingPlace();
		}

		@Override
		public String getToken(MuralPaintingPlace place) {
			return "";
		}
	}
}
