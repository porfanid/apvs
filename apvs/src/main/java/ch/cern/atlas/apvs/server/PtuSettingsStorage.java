package ch.cern.atlas.apvs.server;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public class PtuSettingsStorage {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private static final String APVS_PTU_SETTINGS = "APVS.ptu.settings";
	private static PtuSettingsStorage instance;
	private PtuSettings settings;

	public PtuSettingsStorage(final RemoteEventBus eventBus) {

		load();

		PtuSettingsChangedRemoteEvent.register(eventBus,
				new PtuSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onPtuSettingsChanged(
							PtuSettingsChangedRemoteEvent event) {
						settings = event.getPtuSettings();

						store();
					}
				});
		
		InterventionMapChangedRemoteEvent.subscribe(eventBus, new InterventionMapChangedRemoteEvent.Handler() {
			
			@Override
			public void onInterventionMapChanged(InterventionMapChangedRemoteEvent event) {
				log.info("PTU Setting Storage: PTU IDS changed");
				List<String> activePtuIds = event.getInterventionMap().getPtuIds();

				boolean changed = false;
				for (Iterator<String> i = activePtuIds.iterator(); i
						.hasNext();) {
					boolean added = settings.add(i.next());
					changed |= added;
				}

				if (changed) {
					eventBus.fireEvent(new PtuSettingsChangedRemoteEvent(
							settings));
				}
			}
		});

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				if (event.getRequestedClassName().equals(
						PtuSettingsChangedRemoteEvent.class.getName())) {
					eventBus.fireEvent(new PtuSettingsChangedRemoteEvent(settings));
				}
			}
		});

		eventBus.fireEvent(new PtuSettingsChangedRemoteEvent(settings));
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
			log.warn("Ptu Settings will not be stored");
			return;
		}

		String json = store.getItem(APVS_PTU_SETTINGS);
		if (json != null) {
			settings = (PtuSettings) JsonReader.toJava(json);
		}

		if (settings == null) {
			log.warn("Could not read Ptu Settings, using defaults");
			settings = new PtuSettings();
		}
	}

	private void store() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			return;
		}

		String json = JsonWriter.toJson(settings);
//		log.info("Storing json " + json);

		if (json != null) {
			store.setItem(APVS_PTU_SETTINGS, json);
		}
	}
}
