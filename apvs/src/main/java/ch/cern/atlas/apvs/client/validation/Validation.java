package ch.cern.atlas.apvs.client.validation;

import org.gwtbootstrap3.client.ui.constants.ValidationState;

public class Validation {

	public static ValidationState WARNING = ValidationState.WARNING;
	public static ValidationState ERROR = ValidationState.ERROR;
	public static ValidationState SUCCESS = ValidationState.SUCCESS;

	private ValidationState level;
	private String message;
	
	public Validation() {
		this(SUCCESS, "");
	}
	
	public Validation(ValidationState level, String message) {
		this.level = level;
		this.message = message;
	}
	
	public ValidationState getLevel() {
		return level;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isValid() {
		return level.equals(SUCCESS);
	}
}
