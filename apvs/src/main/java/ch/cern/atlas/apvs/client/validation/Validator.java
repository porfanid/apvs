package ch.cern.atlas.apvs.client.validation;

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;

public interface Validator<T> {
	
	public static ControlGroupType WARNING = ControlGroupType.WARNING;
	public static ControlGroupType ERROR = ControlGroupType.ERROR;
	public static ControlGroupType SUCCESS = ControlGroupType.SUCCESS;
	public static ControlGroupType NONE = ControlGroupType.NONE;
	
	/**
	 * 
	 * @param value
	 * @return null if valid, otherwise error message
	 */
	Validation validate(T value);
}
