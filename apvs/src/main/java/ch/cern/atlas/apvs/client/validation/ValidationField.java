package ch.cern.atlas.apvs.client.validation;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ValidationField<T> extends FormGroup {

	private FormLabel label;
	private Panel container;

	private List<ValidationHandler> handlers = new ArrayList<ValidationHandler>();
	private Validator<T> validator;
	private HelpBlock help;

	public ValidationField(String fieldLabel, Validator<T> validator) {
		label = new FormLabel();
		label.setText(fieldLabel);
		label.addStyleName("col-lg-4");
		this.validator = validator;

		add(label);
		container = new FlowPanel();
		container.addStyleName("col-lg-8");
		add(container);

		help = new HelpBlock();
		
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
			T value = getValue();
			result = validator.validate(value);
		}
		help.setText(result.getMessage());
		setValidationState(result.getLevel());
		if (fireEvents) {
			fire(result.isValid());
		}
		return result.isValid();
	}

	protected void setField(Widget field) {
		container.clear();
		container.add(field);
		container.add(help);
	}

	public abstract T getValue();

	private void fire(boolean valid) {
		for (ValidationHandler handler : handlers) {
			handler.onValid(valid);
		}
	}
}
