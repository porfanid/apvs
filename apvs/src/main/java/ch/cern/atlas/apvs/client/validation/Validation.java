package ch.cern.atlas.apvs.client.validation;

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;

public class Validation {

	public static ControlGroupType WARNING = ControlGroupType.WARNING;
	public static ControlGroupType ERROR = ControlGroupType.ERROR;
	public static ControlGroupType SUCCESS = ControlGroupType.SUCCESS;
	public static ControlGroupType NONE = ControlGroupType.NONE;

	private ControlGroupType level;
	private String message;
	
	public Validation() {
		this(SUCCESS, "");
	}
	
	public Validation(ControlGroupType level, String message) {
		this.level = level;
		this.message = message;
	}
	
	public ControlGroupType getLevel() {
		return level;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isValid() {
		return level.equals(SUCCESS);
	}
}
