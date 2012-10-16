package ch.cern.atlas.apvs.client.widget;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.TextBox;

public class TextBoxField extends ControlGroup {

	private ControlLabel label;
	private Controls controls;
	private TextBox box;
	
	public TextBoxField(String fieldLabel) {
		label = new ControlLabel(fieldLabel);
		controls = new Controls();
		box = new TextBox();
		
		add(label);
		add(controls);
		controls.add(box);
	}
	
	public String getValue() {
		return box.getValue();
	}

}
