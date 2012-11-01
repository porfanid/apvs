package ch.cern.atlas.apvs.client.validation;

public class NotNullValidator implements Validator {

	@Override
	public Validation validate(String value) {
		if (value == null) {
			return new Validation(ERROR, "Cannot be null");
		}
		return new Validation();
	}

}
