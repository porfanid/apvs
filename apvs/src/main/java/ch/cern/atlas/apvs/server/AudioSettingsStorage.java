package ch.cern.atlas.apvs.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.event.AudioSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public class AudioSettingsStorage {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private static final String APVS_ASTERISK_SETTINGS = "APVS.asterisk.settings";
	private static AudioSettingsStorage instance;
	private AudioSettings audioSettings;

	public AudioSettingsStorage(final RemoteEventBus eventBus) {

		load();

		AudioSettingsChangedEvent.register(eventBus, new AudioSettingsChangedEvent.Handler() {
					
					@Override
					public void onAudioSettingsChanged(AudioSettingsChangedEvent event) {
						audioSettings = event.getAudioSettings();
						store();	
					}
		});

		PtuIdsChangedEvent.subscribe(eventBus, new PtuIdsChangedEvent.Handler() {

					@Override
					public void onPtuIdsChanged(PtuIdsChangedEvent event) {
						log.info("Audio Setting Storage: PTU IDS changed");
						List<String> activePtuIds = event.getPtuIds();

						boolean changed = false;
						for (Iterator<String> i = activePtuIds.iterator(); i.hasNext();) {
							boolean added = audioSettings.add(i.next());
							changed |= added;
						}

						if (changed) {
							eventBus.fireEvent(new AudioSettingsChangedEvent(audioSettings));
						}
					}
				});
		
		PtuSettingsChangedEvent.subscribe(eventBus, new PtuSettingsChangedEvent.Handler() {
			
			@Override
			public void onPtuSettingsChanged(PtuSettingsChangedEvent event) {
				PtuSettings ptuSettings = new PtuSettings();
				ptuSettings = event.getPtuSettings();
				List<String> ptuList = new ArrayList<String>(ptuSettings.getPtuIds());
				String ptuId = new String();
				for(int i=0; i<ptuList.size() ; i++){
					ptuId = ptuList.get(i);
					if(audioSettings.contains(ptuId))
						audioSettings.setUsername(ptuId, ptuSettings.getName(ptuId));
				}
				
				eventBus.fireEvent(new AudioSettingsChangedEvent(audioSettings));
			}
		});

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				if (event.getRequestedClassName().equals(AudioSettingsChangedEvent.class.getName())) {
					eventBus.fireEvent(new AudioSettingsChangedEvent(audioSettings));
				}
			}
		});

		eventBus.fireEvent(new AudioSettingsChangedEvent(audioSettings));
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
		log.info("Storing json " + json);

		if (json != null) {
			store.setItem(APVS_ASTERISK_SETTINGS, json);
		}
	}
}
