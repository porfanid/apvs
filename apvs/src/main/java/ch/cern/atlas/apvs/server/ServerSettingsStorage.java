package ch.cern.atlas.apvs.server;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public class ServerSettingsStorage {

	private static final String APVS_SERVER_SETTINGS = "APVS.server.settings";
	private static ServerSettingsStorage instance;
	private ServerSettings settings;

	public ServerSettingsStorage(final RemoteEventBus eventBus) {

		load();

		ServerSettingsChangedEvent.register(eventBus,
				new ServerSettingsChangedEvent.Handler() {

					@Override
					public void onServerSettingsChanged(
							ServerSettingsChangedEvent event) {
						settings = event.getServerSettings();

						store();
					}
				});

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				if (event.getRequestedClassName().equals(
						ServerSettingsChangedEvent.class.getName())) {
					eventBus.fireEvent(new ServerSettingsChangedEvent(settings));
				}
			}
		});

		eventBus.fireEvent(new ServerSettingsChangedEvent(settings));
	}

	public static ServerSettingsStorage getInstance(RemoteEventBus eventBus) {
		if (instance == null) {
			instance = new ServerSettingsStorage(eventBus);
		}
		return instance;
	}

	private void load() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			System.err.println("Server Settings will not be stored");
			return;
		}

		String json = store.getItem(APVS_SERVER_SETTINGS);
		if (json != null) {
			settings = (ServerSettings) JsonReader.toJava(json);
		}

		if (settings == null) {
			System.err
					.println("Could not read Server Settings, using defaults");
			settings = new ServerSettings(true);
		}
	}

	private void store() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			return;
		}

		String json = JsonWriter.toJson(settings);
		System.err.println("Storing json " + json);

		if (json != null) {
			store.setItem(APVS_SERVER_SETTINGS, json);
		}
	}
}
