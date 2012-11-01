package ch.cern.atlas.apvs.client.validation;

public class EmptyValidator implements Validator {

	public EmptyValidator() {
	}
	
	@Override
	public String validate(String value) {
		if (value.length() > 0) {
			return "Length should be 0";
		}
		return null;
	}
}
