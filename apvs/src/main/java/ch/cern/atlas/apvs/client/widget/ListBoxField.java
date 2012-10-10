package ch.cern.atlas.apvs.client.widget;

import java.util.HashMap;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.ListBox;

public class ListBoxField extends ControlGroup {

	private ControlLabel label;
	private Controls controls;
	private ListBox box;
	private Map<String, Integer> ids;
	
	public ListBoxField(String fieldLabel) {
		label = new ControlLabel(fieldLabel);
		controls = new Controls();
		box = new ListBox();
		
		add(label);
		add(controls);
		controls.add(box);
		
		ids = new HashMap<String, Integer>();
	}
	
	public String getValue() {
		return box.getValue();
	}
	
	public Integer getId() {
		return ids.get(box.getValue());
	}

	public void addItem(String item, int id) {
		box.addItem(item);
		ids.put(item, id);
	}
}
