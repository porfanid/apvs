package ch.cern.atlas.apvs.server;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.domain.InterventionMap;
import ch.cern.atlas.apvs.client.event.AudioSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public class AudioSettingsStorage {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private static final String APVS_ASTERISK_SETTINGS = "APVS.asterisk.settings";
	private static AudioSettingsStorage instance;
	private AudioSettings audioSettings;

	public AudioSettingsStorage(final RemoteEventBus eventBus) {

		load();

		AudioSettingsChangedRemoteEvent.register(eventBus, new AudioSettingsChangedRemoteEvent.Handler() {
					
					@Override
					public void onAudioSettingsChanged(AudioSettingsChangedRemoteEvent event) {
						audioSettings = event.getAudioSettings();
						store();	
					}
		});
		
		InterventionMapChangedRemoteEvent.subscribe(eventBus, new InterventionMapChangedRemoteEvent.Handler() {

			@Override
			public void onInterventionMapChanged(
					InterventionMapChangedRemoteEvent event) {
				InterventionMap interventions = event.getInterventionMap();
				boolean changed = false;
				for (Iterator<String> i = interventions.getPtuIds().iterator(); i.hasNext(); ) {
					String ptuId = i.next();
					if(audioSettings.contains(ptuId)) {
						boolean set = audioSettings.setUsername(ptuId, interventions.get(ptuId).getName());
						changed |= set;
					} else {
						boolean added = audioSettings.add(ptuId);
						changed |= added;
					}
				}

				if (changed) {
					eventBus.fireEvent(new AudioSettingsChangedRemoteEvent(audioSettings));
				}
			}
		});
		
		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				if (event.getRequestedClassName().equals(AudioSettingsChangedRemoteEvent.class.getName())) {
					eventBus.fireEvent(new AudioSettingsChangedRemoteEvent(audioSettings));
				}
			}
		});

		eventBus.fireEvent(new AudioSettingsChangedRemoteEvent(audioSettings));
	}

	public static AudioSettingsStorage getInstance(RemoteEventBus eventBus) {
		if (instance == null) {
			instance = new AudioSettingsStorage(eventBus);
		}
		return instance;
	}

	private void load() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			log.warn("Asterisk Settings will not be stored");
			return;
		}

		String json = store.getItem(APVS_ASTERISK_SETTINGS);
		
		
		//************** Audio Settings **************
		if (json != null) {
			audioSettings = (AudioSettings) JsonReader.toJava(json);
		}

		if (audioSettings == null) {
			log.warn("Could not read Audio Settings, using defaults");
			audioSettings = new AudioSettings();
		}
		
	}

	private void store() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			return;
		}

		String json = JsonWriter.toJson(audioSettings);
//		log.info("Storing json " + json);

		if (json != null) {
			store.setItem(APVS_ASTERISK_SETTINGS, json);
		}
	}
}
