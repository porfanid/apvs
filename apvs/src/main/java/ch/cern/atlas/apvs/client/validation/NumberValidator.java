package ch.cern.atlas.apvs.client.validation;

public class NumberValidator implements Validator {

	@Override
	public String validate(String value) {
		try {
			Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return "Not a number";
		}
		return null;
	}
}
