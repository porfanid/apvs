package ch.cern.atlas.apvs.client.validation;

public class NotNullValidator implements Validator {

	@Override
	public String validate(String value) {
		if (value == null) {
			return "Cannot be null";
		}
		return null;
	}

}
