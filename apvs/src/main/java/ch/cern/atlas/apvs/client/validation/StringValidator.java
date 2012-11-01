package ch.cern.atlas.apvs.client.validation;


public class StringValidator implements Validator {

	protected int minLength;
	protected int maxLength;

	public StringValidator(int minLength, int maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
	
	@Override
	public String validate(String value) {
		if (value.length() < minLength) {
			return "Minimum length: "+minLength;
		} else if (value.length() > maxLength) {
			return "Maximum length: "+maxLength;
		}
		return null;
	}
}
