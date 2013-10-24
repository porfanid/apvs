package ch.cern.atlas.apvs.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Fieldset;
import com.google.gwt.event.logical.shared.AttachEvent;

public class ValidationFieldset extends Fieldset {

	private List<ValidationField<?>> fields = new ArrayList<ValidationField<?>>();
	private List<ValidationHandler> handlers = new ArrayList<ValidationHandler>();

	public ValidationFieldset() {
		addAttachHandler(new AttachEvent.Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				validate(true);
			}
		});
	}
	
	public void add(ValidationField<?> field) {
		super.add(field);
		fields.add(field);

		field.addValidationHandler(new ValidationHandler() {

			@Override
			public void onValid(boolean valid) {
				if (valid) {
					for (ValidationField<?> field: fields) {
						valid &= field.validate(false);
					}
				}
				
				for (ValidationHandler handler: handlers) {
					handler.onValid(valid);
				}
			}
		});
	}

	public void addValidationHandler(ValidationHandler validationHandler) {
		handlers.add(validationHandler);
	}
	
	public boolean validate(boolean fireEvents) {
		for (ValidationField<?> field: fields) {
			if (!field.validate(fireEvents)) {
				return false;
			}
		}
		return true;
	}
}
