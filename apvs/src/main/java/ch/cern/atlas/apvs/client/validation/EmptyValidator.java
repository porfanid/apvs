package ch.cern.atlas.apvs.client.validation;

public class EmptyValidator implements Validator {

	public EmptyValidator() {
	}
	
	@Override
	public Validation validate(String value) {
		if (value.length() > 0) {
			return new Validation(ERROR, "Length should be 0");
		}
		return new Validation();
	}
}
