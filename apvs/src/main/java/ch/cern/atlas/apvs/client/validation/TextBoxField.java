package ch.cern.atlas.apvs.client.validation;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class TextBoxField extends FormField {

	private TextBox box;
		
	public TextBoxField(String fieldLabel, Validator validator) {
		super(fieldLabel, validator);
		
		box = new TextBox();
		
		box.addBlurHandler(new BlurHandler() {		
			@Override
			public void onBlur(BlurEvent event) {
				validate();
			}
		});
		
		box.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				validate();
			}
		});
		
		box.addKeyUpHandler(new KeyUpHandler() {		
			@Override
			public void onKeyUp(KeyUpEvent event) {
				validate();
			}
		});
				
		setField(box);
	}
	
	public TextBoxField(String fieldLabel) {
		this(fieldLabel, null);
	}
		
	public String getValue() {
		return box.getValue();
	}
	
}
