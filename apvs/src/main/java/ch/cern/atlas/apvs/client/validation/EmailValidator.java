package ch.cern.atlas.apvs.client.validation;

public class EmailValidator implements Validator {
	
	private String info;

	public EmailValidator(String info) {
		this.info = info;
	}

	@Override
	public Validation validate(String value) {
		if (value == null || value.equals("")) {
			return new Validation(NONE, info);
		} else if (!value.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
			return new Validation(ERROR, "Not a valid e-mail");
		}
		return new Validation();
	}

}
