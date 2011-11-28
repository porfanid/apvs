package ch.cern.atlas.apvs.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class ProcedureView extends SimplePanel {

	private final String procedureURL = "http://localhost:8890/avps-proc";
	private String procedure = "mural-m4v";
	private int step = 1;
	private String extension = ".m4v";
	
	public ProcedureView() {
		String source = procedureURL + "/" + procedure +"/" + step + extension;
		setWidget(new HTML(
				"<video width='640' height='360' poster='poster.jpg' controls autoplay>"
						+ "<source src='"+source+"' type='video/mp4'></source>"
						+ "</video>"));
	}
}
