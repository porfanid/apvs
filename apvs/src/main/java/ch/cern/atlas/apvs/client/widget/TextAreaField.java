package ch.cern.atlas.apvs.client.widget;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.TextArea;

public class TextAreaField extends ControlGroup {

	private ControlLabel label;
	private Controls controls;
	private TextArea area;
	
	public TextAreaField(String fieldLabel) {
		label = new ControlLabel(fieldLabel);
		controls = new Controls();
		area = new TextArea();
		
		add(label);
		add(controls);
		controls.add(area);
	}
	
	public String getValue() {
		return area.getValue();
	}

}
