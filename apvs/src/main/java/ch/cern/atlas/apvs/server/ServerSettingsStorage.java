package ch.cern.atlas.apvs.server;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.ServerPwds;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

public class ServerSettingsStorage {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private static final String APVS_SERVER_SETTINGS = "APVS.server.settings";
	private static final String APVS_SERVER_PWDS = "APVS.server.pwds";
	private static ServerSettingsStorage instance;
	private ServerSettings settings;
	private ServerPwds pwds;
	
	private RemoteEventBus eventBus;

	private ServerSettingsStorage(final RemoteEventBus eventBus) {

		load();

		ServerSettingsChangedRemoteEvent.register(eventBus,
				new ServerSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onServerSettingsChanged(
							ServerSettingsChangedRemoteEvent event) {
						settings = event.getServerSettings();

						store();
					}
				});

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				if (event.getRequestedClassName().equals(
						ServerSettingsChangedRemoteEvent.class.getName())) {
					eventBus.fireEvent(new ServerSettingsChangedRemoteEvent(
							(ServerSettings) settings));
				}
			}
		});

	}

	public static ServerSettingsStorage getInstance(RemoteEventBus eventBus) {
		if (instance == null) {
			instance = new ServerSettingsStorage(eventBus);
		}
		return instance;
	}

	public ServerPwds getPasswords() {
		return pwds;
	}

	private void load() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			log.warn("Server Settings will not be stored");
			return;
		}

		settings = new ServerSettings(true);

		for (Iterator<String> i = store.getKeys(APVS_SERVER_SETTINGS).iterator(); i
				.hasNext();) {
			String key = i.next();
			settings.put(key, store.getString(APVS_SERVER_SETTINGS+"."+key));
		}
		
		settings.put(ServerSettings.Entry.databaseUrl.toString(), Database.getInstance(eventBus).getConfiguration().getProperty("connection.url"));

		log.info("Server Settings Read");

		pwds = new ServerPwds(true);
		for (Iterator<String> i = store.getKeys(APVS_SERVER_PWDS).iterator(); i.hasNext();) {
			String key = i.next();
			pwds.put(key, store.getString(APVS_SERVER_PWDS+"."+key));
		}
	}

	private void store() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			return;
		}

		for (Iterator<String> i = settings.getKeys().iterator(); i.hasNext();) {
			String key = i.next();
			if (!key.equals(ServerSettings.Entry.databaseUrl.toString())) {
				store.setItem(APVS_SERVER_SETTINGS + "." + key, settings.get(key));
			}
		}

		for (Iterator<String> i = pwds.getKeys().iterator(); i.hasNext();) {
			String key = i.next();
			store.setItem(APVS_SERVER_PWDS + "." + key, pwds.get(key));
		}
	}

	public void setPassword(String name, String password) {
		System.err.println("Storing " + name + " " + password);
		pwds.put(name, password);
		store();
	}
}
