package ch.cern.atlas.apvs.client.settings;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;

public class ServerSettings extends AbstractServerSettings {

	private static final long serialVersionUID = -8089892467523033522L;
	
	public static final String[] settingNames = { "PTU URL", "Dosimeter URL",
			"Procedure URL", "Database URL" };
	@SuppressWarnings("rawtypes")
	public static final Class[] cellClass = { TextInputCell.class,
		TextInputCell.class, TextInputCell.class, TextInputCell.class };
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
		put(settingNames[2], "http://localhost:8890/apvs-procs/procedures");
		put(settingNames[3], "PTU/atlas@//pcatlaswpss03.cern.ch:1521/XE");
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("ServerSettings: ");
		for (int i=0; i<settingNames.length; i++) {
			sb.append(settingNames[i]+"="+get(settingNames[i])+"; ");
		}
		return sb.toString();
	}
}
