package ch.cern.atlas.apvs.client.validation;

public class IntegerValidator implements Validator<String> {

	private String info;

	public IntegerValidator(String info) {
		this.info = info;
	}

	@Override
	public Validation validate(String value) {
		if (value == null || value.equals("")) {
			return new Validation(NONE, info);
		} else if (!value.matches("^\\d+$")) {
			return new Validation(ERROR, "Not a valid number");
		}
		return new Validation();
	}

}
