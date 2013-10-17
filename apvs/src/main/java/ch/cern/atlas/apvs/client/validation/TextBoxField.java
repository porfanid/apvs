package ch.cern.atlas.apvs.client.validation;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.svenjacobs.gwtbootstrap3.client.ui.TextBox;

public class TextBoxField extends ValidationField {

	private TextBox box;
		
	public TextBoxField(String fieldLabel, Validator validator) {
		super(fieldLabel, validator);
		
		box = new TextBox() {
			@Override
			public void onBrowserEvent(Event event) {
				super.onBrowserEvent(event);
				Window.alert(" "+event);
			}
		};
		
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
	
	public TextBoxField(String fieldLabel) {
		this(fieldLabel, null);
	}
		
	public String getValue() {
		return box.getValue();
	}
	
}
