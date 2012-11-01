package ch.cern.atlas.apvs.client.validation;

public class IntegerValidator implements Validator {

	@Override
	public String validate(String value) {
		if (!value.matches("^\\d+$")) {
			return "Not a valid number";
		}
		return null;
	}

}
