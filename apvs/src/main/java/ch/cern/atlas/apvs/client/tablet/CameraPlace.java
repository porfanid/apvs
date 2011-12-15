package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.places.SharedPlace;

import com.google.gwt.place.shared.PlaceTokenizer;

public class CameraPlace extends SharedPlace {

	private static final long serialVersionUID = 6851156765106620583L;
	private int type;
	
	public CameraPlace() {
	}
	
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
