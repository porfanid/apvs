package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.places.SharedPlace;

import com.google.gwt.place.shared.PlaceTokenizer;

public class CameraPlace extends SharedPlace {

	private static final long serialVersionUID = 6851156765106620583L;
	private String type;
	
	public CameraPlace() {
	}
	
	public CameraPlace(String type) {
		this.type = type;
	}
	
	public static class CameraPlaceTokenizer implements
			PlaceTokenizer<CameraPlace> {

		@Override
		public CameraPlace getPlace(String token) {
			return new CameraPlace(token);
		}

		@Override
		public String getToken(CameraPlace place) {
			return place.getType();
		}
	}

	public String getType() {
		return type;
	}
}
