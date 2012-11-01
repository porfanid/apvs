package ch.cern.atlas.apvs.client.validation;

public interface Validator {
	/**
	 * 
	 * @param value
	 * @return null if valid, otherwise error message
	 */
	String validate(String value);
}
