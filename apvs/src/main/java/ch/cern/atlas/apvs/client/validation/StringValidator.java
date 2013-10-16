package ch.cern.atlas.apvs.client.validation;


public class StringValidator implements Validator {

	protected int minLength;
	protected int maxLength;
	protected String info;

	public StringValidator(int minLength, int maxLength, String info) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.info = info;
	}
	
	@Override
	public Validation validate(String value) {
		if (value == null || value.length() == 0) {
			return new Validation(WARNING, info);
		} else if (value.length() < minLength) {
			return new Validation(ERROR, "Minimum length: "+minLength);
		} else if (value.length() > maxLength) {
			return new Validation(ERROR, "Maximum length: "+maxLength);
		}
		return new Validation(SUCCESS, "");
	}
}
