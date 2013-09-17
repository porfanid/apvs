package ch.cern.atlas.apvs.client.settings;

import ch.cern.atlas.apvs.client.settings.ServerSettings.Entry;


public class ServerPwds extends AbstractServerSettings {
	
	private static final long serialVersionUID = -1621754534990786349L;

	public ServerPwds() {
		this(false);
	}
	
	public ServerPwds(boolean setDefaults) {
		if (setDefaults) {
			put(Entry.audioUrl.toString(), "");
		}
	}	
}
