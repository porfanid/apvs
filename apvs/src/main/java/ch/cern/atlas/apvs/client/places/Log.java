package ch.cern.atlas.apvs.client.places;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class Log extends MenuPlace {

	private static final long serialVersionUID = -3084540177666785852L;

	public int getIndex() {
		return 5;
	}

	@Override
	public String getHeader() {
		return "Log";
	}

	@Override
	public Widget getWidget() {
		Panel panel = new VerticalPanel();

		RadioButton error = new RadioButton("log", "Error");
		panel.add(error);

		RadioButton info = new RadioButton("log", "Info");
		panel.add(info);

		RadioButton warning = new RadioButton("log", "Warning");
		panel.add(warning);

		RadioButton debug = new RadioButton("log", "Debug");
		panel.add(debug);

		RadioButton all = new RadioButton("log", "All");
		panel.add(all);

		error.setValue(true);

		return new SimplePanel(panel);
	}

}
