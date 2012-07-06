package ch.cern.atlas.apvs.obsolete;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.server.ServerStorage;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public class SupervisorSettingsStorage {

	private static final String APVS_SUPERVISOR_SETTINGS = "APVS.supervisor.settings";
	private static SupervisorSettingsStorage instance;
	private SupervisorSettings settings;

	public SupervisorSettingsStorage(final RemoteEventBus eventBus) {

		load();

		SupervisorSettingsChangedEvent.register(eventBus,
				new SupervisorSettingsChangedEvent.Handler() {

					@Override
					public void onSupervisorSettingsChanged(
							SupervisorSettingsChangedEvent event) {
						settings = event.getSupervisorSettings();

						store();
					}
				});

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				if (event.getRequestedClassName().equals(
						SupervisorSettingsChangedEvent.class.getName())) {
					eventBus.fireEvent(new SupervisorSettingsChangedEvent(settings));
				}
			}
		});

		eventBus.fireEvent(new SupervisorSettingsChangedEvent(settings));
	}

	public static SupervisorSettingsStorage getInstance(RemoteEventBus eventBus) {
		if (instance == null) {
			instance = new SupervisorSettingsStorage(eventBus);
		}
		return instance;
	}

	private void load() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			System.err.println("Supervisor Settings will not be stored");
			return;
		}

		String json = store.getItem(APVS_SUPERVISOR_SETTINGS);
		if (json != null) {
			settings = (SupervisorSettings) JsonReader.toJava(json);
		}

		if (settings == null) {
			System.err
					.println("Could not read Supervisor Settings, using defaults");
			settings = new SupervisorSettings(true);
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
			store.setItem(APVS_SUPERVISOR_SETTINGS, json);
		}
	}
}
