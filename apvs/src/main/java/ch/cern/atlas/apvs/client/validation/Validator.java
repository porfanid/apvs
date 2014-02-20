package ch.cern.atlas.apvs.client.validation;

import org.gwtbootstrap3.client.ui.constants.ValidationState;

public interface Validator<T> {
	
	public static ValidationState WARNING = ValidationState.WARNING;
	public static ValidationState ERROR = ValidationState.ERROR;
	public static ValidationState SUCCESS = ValidationState.SUCCESS;
	
	/**
	 * 
	 * @param value
	 * @return null if valid, otherwise error message
	 */
	Validation validate(T value);
}
