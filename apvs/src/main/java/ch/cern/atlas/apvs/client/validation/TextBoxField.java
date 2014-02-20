package ch.cern.atlas.apvs.client.validation;

import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;

public class TextBoxField extends ValidationField<String> {

	private TextBox box;
		
	public TextBoxField(String fieldLabel, Validator<String> validator) {
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
		return box.getValue() != null ? box.getValue().trim() : null;
	}
	
}
