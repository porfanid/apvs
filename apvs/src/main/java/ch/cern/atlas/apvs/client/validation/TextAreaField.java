package ch.cern.atlas.apvs.client.validation;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class TextAreaField extends ValidationField {

	private TextArea area;
	
	public TextAreaField(String fieldLabel, Validator validator) {
		super(fieldLabel, validator);
		
		area = new TextArea();
		
		area.addBlurHandler(new BlurHandler() {		
			@Override
			public void onBlur(BlurEvent event) {
				validate();
			}
		});
		
		area.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				validate();
			}
		});
		
		area.addKeyUpHandler(new KeyUpHandler() {		
			@Override
			public void onKeyUp(KeyUpEvent event) {
				validate();
			}
		});
		
		setField(area);
	}
	
	public TextAreaField(String fieldLabel) {
		this(fieldLabel, null);
	}
	
	public String getValue() {
		return area.getValue();
	}

}
