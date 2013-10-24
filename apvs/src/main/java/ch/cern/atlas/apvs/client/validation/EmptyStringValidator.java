package ch.cern.atlas.apvs.client.validation;

public class EmptyStringValidator implements Validator<String> {

	public EmptyStringValidator() {
	}
	
	@Override
	public Validation validate(String value) {
		if (value.length() > 0) {
			return new Validation(ERROR, "Length should be 0");
		}
		return new Validation();
	}
}
