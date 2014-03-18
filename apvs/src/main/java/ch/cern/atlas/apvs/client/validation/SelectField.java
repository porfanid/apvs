package ch.cern.atlas.apvs.client.validation;

import java.util.HashMap;
import java.util.Map;

import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class SelectField extends ValidationField<String> {

	private Select select;
	private Map<String, Integer> ids;
	
	public SelectField(String fieldLabel, Validator<String> validator) {
		super(fieldLabel, validator);
	    select = new Select();
		
		setField(select);
		
		ids = new HashMap<String, Integer>();
		
//		select.addBlurHandler(new BlurHandler() {
//			
//			@Override
//			public void onBlur(BlurEvent event) {
//				validate(true);
//			}
//		});
//		
//		select.addFocusHandler(new FocusHandler() {
//			
//			@Override
//			public void onFocus(FocusEvent event) {
//				validate(true);
//			}
//		});
		
		select.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				validate(true);
			}
		});
	}
	
	public SelectField(String fieldLabel) {
		this(fieldLabel, null);
	}
	
	public String getValue() {
		return select.getValue();
	}
	
	public Integer getId() {
		return ids.get(getValue());
	}

	public void addItem(String item, int id) {
		Option option = new Option();
		option.setText(item);
		select.add(option);
		ids.put(item, id);
		validate(true);
	}	
}
