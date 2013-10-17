package ch.cern.atlas.apvs.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.HasEnabled;
import com.svenjacobs.gwtbootstrap3.client.ui.Form;
import com.svenjacobs.gwtbootstrap3.client.ui.HasType;
import com.svenjacobs.gwtbootstrap3.client.ui.ModalComponent;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.ButtonType;
import com.svenjacobs.gwtbootstrap3.client.ui.constants.FormType;

public class ValidationForm extends Form implements ModalComponent {

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
				validate(true);
			}
		});
		
		setType(FormType.HORIZONTAL);
	}

	public void add(ValidationFieldset fieldset) {
		super.add(fieldset);
		fieldsets.add(fieldset);

		fieldset.addValidationHandler(new ValidationHandler() {

			@Override
			public void onValid(boolean valid) {
				if (valid) {
					for (ValidationFieldset fieldset: fieldsets) {
						valid &= fieldset.validate(false);
					}
				}
				
				for (ValidationHandler handler : handlers) {
					handler.onValid(valid);
				}
			}
		});
	}

	public void addValidationHandler(ValidationHandler validationHandler) {
		handlers.add(validationHandler);
	}

	public boolean validate(boolean fireEvents) {
		for (ValidationFieldset fieldset : fieldsets) {
			if (!fieldset.validate(fireEvents)) {
				return false;
			}
		}
		return true;
	}

}
