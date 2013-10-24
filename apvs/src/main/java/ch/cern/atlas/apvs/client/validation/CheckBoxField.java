package ch.cern.atlas.apvs.client.validation;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.CheckBox;

public class CheckBoxField extends ValidationField<Boolean> {

	private CheckBox box;
		
	public CheckBoxField(String fieldLabel, Validator<Boolean> validator) {
		super(fieldLabel, validator);
		
		box = new CheckBox();
		
		box.addBlurHandler(new BlurHandler() {		
			@Override
			public void onBlur(BlurEvent event) {
				validate(true);
			}
		});
		
		box.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				validate(true);
			}
		});
		
		box.addKeyUpHandler(new KeyUpHandler() {		
			@Override
			public void onKeyUp(KeyUpEvent event) {
				validate(true);
			}
		});
				
		setField(box);
	}
	
	public CheckBoxField(String fieldLabel) {
		this(fieldLabel, null);
	}
		
	public Boolean getValue() {
		return box.getValue();
	}
	
}
