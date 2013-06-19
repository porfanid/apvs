package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.places.SharedPlace;

import com.google.gwt.place.shared.PlaceTokenizer;

public class ImagePlace extends SharedPlace {
	
	private static final long serialVersionUID = 6045295669718915907L;
	private String name;
	private String url;

	public ImagePlace() {
	}
	
	public ImagePlace(String name, String url) {
		this.name = name;
		this.url = url;
	}
	
	public static class ImagePlaceTokenizer implements
			PlaceTokenizer<ImagePlace> {

		@Override
		public ImagePlace getPlace(String token) {
			String[] s = token.split(";", 2);
			return new ImagePlace(s[0], s.length > 1 ? s[1] : "");
		}

		@Override
		public String getToken(ImagePlace place) {
			return place.getName()+";"+place.getUrl();
		}
	}
	
	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
