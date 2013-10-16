package ch.cern.atlas.apvs.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.Widget;
import com.svenjacobs.gwtbootstrap3.client.ui.FormGroup;
import com.svenjacobs.gwtbootstrap3.client.ui.FormLabel;

public abstract class ValidationField extends FormGroup {

	private FormLabel label;
	private Widget controls;

	private List<ValidationHandler> handlers = new ArrayList<ValidationHandler>();
	private Validator validator;
//	private HelpInline help;

	public ValidationField(String fieldLabel, Validator validator) {
		label = new FormLabel();
		label.setText(fieldLabel);
		this.validator = validator;

//		controls = new FormElementContainer();
//		help = new HelpInline();

		add(label);
		add(controls);

		addAttachHandler(new AttachEvent.Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				validate(true);
			}
		});
	}

	public void addValidationHandler(ValidationHandler handler) {
		handlers.add(handler);
	}

	public boolean validate(boolean fireEvents) {
		Validation result = new Validation();
		if (validator != null) {
			String value = getValue() != null ? getValue().trim() : null;
			result = validator.validate(value);
		}
//		help.setText(result.getMessage());
		setValidationState(result.getLevel());
		if (fireEvents) {
			fire(result.isValid());
		}
		return result.isValid();
	}

	protected void setField(Widget field) {
		controls = field;
//		controls.add(help);
	}

	public abstract String getValue();

	private void fire(boolean valid) {
		for (ValidationHandler handler : handlers) {
			handler.onValid(valid);
		}
	}
}
