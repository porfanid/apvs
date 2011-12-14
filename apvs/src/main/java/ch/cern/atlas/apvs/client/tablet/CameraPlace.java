package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class CameraPlace extends Place {
	private int type;
	
	public CameraPlace(int type) {
		this.type = type;
	}
	
	public static class CameraPlaceTokenizer implements
			PlaceTokenizer<CameraPlace> {

		@Override
		public CameraPlace getPlace(String token) {
			return new CameraPlace(Integer.parseInt(token));
		}

		@Override
		public String getToken(CameraPlace place) {
			return Integer.toString(place.getType());
		}
	}

	public int getType() {
		return type;
	}
}
