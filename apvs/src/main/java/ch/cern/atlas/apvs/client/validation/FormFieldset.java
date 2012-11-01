package ch.cern.atlas.apvs.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Fieldset;
import com.google.gwt.event.logical.shared.AttachEvent;

public class FormFieldset extends Fieldset {

	private List<FormField> fields = new ArrayList<FormField>();
	private List<ValidationHandler> handlers = new ArrayList<ValidationHandler>();

	public FormFieldset() {
		addAttachHandler(new AttachEvent.Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				validate();
			}
		});
	}
	
	public void add(FormField field) {
		super.add(field);
		fields.add(field);

		field.addValidationHandler(new ValidationHandler() {

			@Override
			public void onValid(boolean valid) {
				for (ValidationHandler handler: handlers) {
					handler.onValid(valid);
				}
			}
		});
	}

	public void addValidationHandler(ValidationHandler validationHandler) {
		handlers.add(validationHandler);
	}
	
	public boolean validate() {
		for (FormField field: fields) {
			if (!field.validate()) {
				return false;
			}
		}
		return true;
	}
}
