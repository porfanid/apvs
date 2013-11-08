package ch.cern.atlas.apvs.client.settings;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;

public class ServerSettings extends AbstractServerSettings {

	private static final long serialVersionUID = -8089892467523033522L;
	
	public enum Entry {
		ptuUrl("PTU URL", TextCell.class, TextCell.class),
		videoUrl("Video URL", TextCell.class, TextCell.class),
		databaseUrl("Database URL", TextCell.class, TextCell.class),
		audioUrl("Audio URL", TextCell.class, TextCell.class),
		procedureUrl("Procedure URL", TextCell.class, TextCell.class);
		private String s;
		private Class<?> c;
		private Class<?> n;
		
		private Entry(String s) {
			this(s, TextInputCell.class, TextCell.class);
		}
		
		@SuppressWarnings({ "rawtypes" }) 
		private Entry(String s, Class c, Class n) {
			this.s = s;
			this.c = c;
			this.n = n;
		}
		
		private Class<?> getCellClass() {
			return c;
		}

		private Class<?> getNameClass() {
			return n;
		}
		
		public String toString() {
			return s;
		}
		
		public static List<String> getKeys() {
			List<String> r = new ArrayList<String>(values().length);
			for (Entry k:values()) {
				r.add(k.toString());
			}
			return r;
		}
		
		public static List<Class<?>> getCellClasses() {
			List<Class<?>> r = new ArrayList<Class<?>>(values().length);
			for (Entry k:values()) {
				r.add(k.getCellClass());
			}
			return r;
		}
		
		public static List<Class<?>> getNameClasses() {
			List<Class<?>> r = new ArrayList<Class<?>>(values().length);
			for (Entry k:values()) {
				r.add(k.getNameClass());
			}
			return r;
		}	
	}
	
	public ServerSettings() {
		this(false);
	}

	public ServerSettings(boolean setDefaults) {
		if (!setDefaults)
			return;

		put(Entry.ptuUrl.toString(), "wpss-sensor-server:10124");
		put(Entry.videoUrl.toString(), "wpss-video-server:20000");
		put(Entry.databaseUrl.toString(), "--");
		put(Entry.audioUrl.toString(), "manager@wpss-sensor-server.cern.ch");
		put(Entry.procedureUrl.toString(), "");
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("ServerSettings: ");
		for (Entry k:Entry.values()) {
			sb.append(k.toString()+"="+get(k.toString())+"; ");
		}
		return sb.toString();
	}
}
