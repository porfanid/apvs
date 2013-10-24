package ch.cern.atlas.apvs.client.validation;

public class NumberValidator implements Validator<String> {

	private String info;

	public NumberValidator(String info) {
		this.info = info;
	}
	
	@Override
	public Validation validate(String value) {
		if (value == null || value.equals("")) {
			return new Validation(NONE, info);
		}
		try {
			Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return new Validation(ERROR, "Not a number");
		}
		return new Validation();
	}
}
