package ch.cern.atlas.apvs.client.validation;

import org.gwtbootstrap3.client.ui.Input;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class InputField extends ValidationField<String> {
	private Input input;
		
	public InputField(String fieldLabel, Validator<String> validator) {
		super(fieldLabel, validator);

		input = new Input();
		
		input.addKeyUpHandler(new KeyUpHandler() {		
			@Override
			public void onKeyUp(KeyUpEvent event) {
				validate(true);
			}
		});
		
		setField(input);
	}
	
	public InputField(String fieldLabel) {
		this(fieldLabel, null);
	}
	
	public String getValue() {
		return input.getValue() != null ? input.getValue().trim() : null;
	}

	public Input getField() {
		return input;
	}
	
}
