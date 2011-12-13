package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class RadiationMapPlace extends Place {
	public static class RadiationMapPlaceTokenizer implements
			PlaceTokenizer<RadiationMapPlace> {

		@Override
		public RadiationMapPlace getPlace(String token) {
			return new RadiationMapPlace();
		}

		@Override
		public String getToken(RadiationMapPlace place) {
			return "";
		}
	}
}
