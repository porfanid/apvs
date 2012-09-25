package ch.cern.atlas.apvs.client.settings;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;

public class ServerSettings extends AbstractServerSettings {

	private static final long serialVersionUID = -8089892467523033522L;
	
	public enum Key {
		ptuUrl("PTU URL"),
		dosimeterUrl("Dosimeter URL"),
		procedureUrl("Procedure URL"),
		databaseUrl("Database URL");
		private String s;
		
		private Key(String s) {
			this.s = s;
		}
		
		public String toString() {
			return s;
		}
		
		public static List<String> getKeys() {
			List<String> r = new ArrayList<String>(values().length);
			for (Key k:values()) {
				r.add(k.toString());
			}
			return r;
		}
	}
	
//	public static final String[] settingNames = { "PTU URL", "Dosimeter URL",
//			"Procedure URL", "Database URL" };
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
		put(Key.ptuUrl.toString(), "localhost:4005");
		put(Key.dosimeterUrl.toString(), "localhost:4001");
		put(Key.procedureUrl.toString(), "http://localhost:8890/apvs-procs/procedures");
		put(Key.databaseUrl.toString(), "PTU/atlas@//pcatlaswpss03.cern.ch:1521/XE");
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("ServerSettings: ");
		for (Key k:Key.values()) {
			sb.append(k.toString()+"="+get(k.toString())+"; ");
		}
		return sb.toString();
	}
}
