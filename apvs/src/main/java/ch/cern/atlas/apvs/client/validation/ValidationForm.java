package ch.cern.atlas.apvs.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.base.HasType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.HasEnabled;

public class ValidationForm extends Form {

	private List<ValidationFieldset> fieldsets = new ArrayList<ValidationFieldset>();
	private List<ValidationHandler> handlers = new ArrayList<ValidationHandler>();

	public ValidationForm() {
	}

	public ValidationForm(final HasEnabled ok, final HasEnabled cancel) {
		
		addValidationHandler(new ValidationHandler() {

			@SuppressWarnings("unchecked")
			@Override
			public void onValid(boolean valid) {
				ok.setEnabled(valid);
				if (ok instanceof HasType<?>) {
					((HasType<ButtonType>)ok).setType(valid ? ButtonType.PRIMARY : ButtonType.DEFAULT);
				}
				if (cancel instanceof HasType<?>) {
					((HasType<ButtonType>)cancel).setType(valid ? ButtonType.DEFAULT : ButtonType.PRIMARY);
				}
			}
		});
		
		addAttachHandler(new AttachEvent.Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				validate();
			}
		});
	}

	public void add(ValidationFieldset fieldset) {
		super.add(fieldset);
		fieldsets.add(fieldset);

		fieldset.addValidationHandler(new ValidationHandler() {

			@Override
			public void onValid(boolean valid) {
				for (ValidationHandler handler : handlers) {
					handler.onValid(valid);
				}
			}
		});
	}

	public void addValidationHandler(ValidationHandler validationHandler) {
		handlers.add(validationHandler);
	}

	public boolean validate() {
		for (ValidationFieldset fieldset : fieldsets) {
			if (!fieldset.validate()) {
				return false;
			}
		}
		return true;
	}

}
