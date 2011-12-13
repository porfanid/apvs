package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ProcedurePlace extends Place {
	
	private String url;
	private String name;
	private String step;

	public ProcedurePlace(String url, String name, String step) {
		this.url = url;
		this.name = name;
		this.step = step;
	}
	
	public static class ProcedurePlaceTokenizer implements
			PlaceTokenizer<ProcedurePlace> {

		@Override
		public ProcedurePlace getPlace(String token) {
			String[] s = token.split(";", 3);
			return new ProcedurePlace(s[0], s.length > 1 ? s[1] : "", s.length > 2 ? s[2] : "");
		}

		@Override
		public String getToken(ProcedurePlace place) {
			return place.getUrl()+";"+place.getName()+";"+place.getStep();
		}
	}
	
	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

	public String getStep() {
		return step;
	}
}
