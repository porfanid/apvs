package ch.cern.atlas.apvs.server;

import java.util.Iterator;
import java.util.List;

import ch.cern.atlas.apvs.client.event.PtuSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public class PtuSettingsStorage {

	private static final String APVS_PTU_SETTINGS = "APVS.ptu.settings";
	private static PtuSettingsStorage instance;
	private PtuSettings settings;

	public PtuSettingsStorage(final RemoteEventBus eventBus) {

		load();

		PtuSettingsChangedEvent.register(eventBus,
				new PtuSettingsChangedEvent.Handler() {

					@Override
					public void onPtuSettingsChanged(
							PtuSettingsChangedEvent event) {
						settings = event.getPtuSettings();

						store();
					}
				});
		
		PtuIdsChangedEvent.subscribe(eventBus,
				new PtuIdsChangedEvent.Handler() {

					@Override
					public void onPtuIdsChanged(PtuIdsChangedEvent event) {
						System.err.println("PTU Setting Storage: PTU IDS changed");
						List<Integer> activePtuIds = event.getPtuIds();

						boolean changed = false;
						for (Iterator<Integer> i = activePtuIds.iterator(); i
								.hasNext();) {
							boolean added = settings.add(i.next());
							changed |= added;
						}
						
						if (changed) {
							eventBus.fireEvent(new PtuSettingsChangedEvent(settings));
						}
					}
				});

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				if (event.getRequestedClassName().equals(
						PtuSettingsChangedEvent.class.getName())) {
					eventBus.fireEvent(new PtuSettingsChangedEvent(settings));
				}
			}
		});

		eventBus.fireEvent(new PtuSettingsChangedEvent(settings));
	}

	public static PtuSettingsStorage getInstance(RemoteEventBus eventBus) {
		if (instance == null) {
			instance = new PtuSettingsStorage(eventBus);
		}
		return instance;
	}

	private void load() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			System.err.println("Ptu Settings will not be stored");
			return;
		}

		String json = store.getItem(APVS_PTU_SETTINGS);
		if (json != null) {
			settings = (PtuSettings) JsonReader.toJava(json);
		}

		if (settings == null) {
			System.err
					.println("Could not read Ptu Settings, using defaults");
			settings = new PtuSettings();
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
			store.setItem(APVS_PTU_SETTINGS, json);
		}
	}
}
