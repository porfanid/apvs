package ch.cern.atlas.apvs.client;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.TextCell;

public class ServerSettings extends AbstractServerSettings {

	private static final long serialVersionUID = -8089892467523033522L;

	public static final String[] settingNames = { "PTU URL", "Dosimeter URL",
			"Procedure URL", "Show Fake Measurements" };
	@SuppressWarnings("rawtypes")
	public static final Class[] cellClass = { EditTextCell.class,
			EditTextCell.class, EditTextCell.class, CheckboxCell.class };
	@SuppressWarnings("rawtypes")
	public static final Class[] nameClass = { TextCell.class, TextCell.class,
			TextCell.class, TextCell.class };

	public ServerSettings() {
		this(false);
	}

	public ServerSettings(boolean setDefaults) {
		if (!setDefaults)
			return;
		put(settingNames[0], "localhost:4005");
		put(settingNames[1], "localhost:4001");
		put(settingNames[2],
		"http://localhost:8890/apvs-procs/procedures");
		put(settingNames[3], true);
	}
}
