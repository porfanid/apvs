package ch.cern.atlas.apvs.server;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.domain.InterventionMap;
import ch.cern.atlas.apvs.client.event.AudioUsersSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.client.settings.VoipAccount;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.cedarsoftware.util.io.JsonReader;

public class AudioUsersSettingsStorage {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private static final String APVS_AUDIO_USERS_SETTINGS = "APVS.audioUsers.settings";
	private static AudioUsersSettingsStorage instance;
	private AudioSettings audioSettings;

	public AudioUsersSettingsStorage(final RemoteEventBus eventBus) {

		load();

		AudioUsersSettingsChangedRemoteEvent.register(eventBus,
				new AudioUsersSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onAudioUsersSettingsChanged(
							AudioUsersSettingsChangedRemoteEvent event) {
						audioSettings = event.getAudioSettings();
						store();
					}
				});

		InterventionMapChangedRemoteEvent.subscribe(eventBus,
				new InterventionMapChangedRemoteEvent.Handler() {

			@Override
			public void onInterventionMapChanged(InterventionMapChangedRemoteEvent event) {
				InterventionMap interventions = event.getInterventionMap();
				boolean changed = false;
				
				for (Iterator<String> i = interventions.getPtuIds().iterator(); i.hasNext(); ) {
					String ptuId = i.next();
					if(audioSettings.contains(ptuId)) {
						boolean set = audioSettings.setIntervention(ptuId, interventions.get(ptuId));
						System.out.println("Resultado de set: "+ set);
						//boolean set = audioSettings.setUsername(ptuId, interventions.get(ptuId).getName());
						changed |= set;
					}else{
						boolean added = audioSettings.add(ptuId);
						changed |= added;
					}
				}
				
				if (changed) {
					eventBus.fireEvent(new AudioUsersSettingsChangedRemoteEvent(audioSettings));
				}
			}
		});

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				if (event.getRequestedClassName().equals(
						AudioUsersSettingsChangedRemoteEvent.class.getName())) {
					eventBus.fireEvent(new AudioUsersSettingsChangedRemoteEvent(
							audioSettings));
				}
			}
		});

		eventBus.fireEvent(new AudioUsersSettingsChangedRemoteEvent(
				audioSettings));
	}

	public static AudioUsersSettingsStorage getInstance(RemoteEventBus eventBus) {
		if (instance == null) {
			instance = new AudioUsersSettingsStorage(eventBus);
		}
		return instance;
	}

	private void load() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			log.warn("Users Audio Settings will not be stored");
			return;
		}
		
		audioSettings = new AudioSettings();
		for (Iterator<String> i = store.getKeys(APVS_AUDIO_USERS_SETTINGS).iterator(); i.hasNext(); ) {
			String ptuId = i.next();
			
			audioSettings.add(ptuId);

			audioSettings.setUsername(ptuId, store.getString(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".username"));
			audioSettings.setNumber(ptuId, store.getString(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".account"));
			audioSettings.setChannel(ptuId, store.getString(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".channel"));
			audioSettings.setDestUser(ptuId, store.getString(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".destUser"));
			audioSettings.setDestPTU(ptuId, store.getString(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".destPTU"));
			audioSettings.setStatus(ptuId, store.getBoolean(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".status"));
			audioSettings.setOnCall(ptuId, store.getBoolean(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".onCall"));
			audioSettings.setActivity(ptuId, store.getString(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".activity"));
			audioSettings.setRoom(ptuId, store.getString(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".room"));
			audioSettings.setMute(ptuId, store.getBoolean(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".mute"));
			audioSettings.setOnConference(ptuId, store.getBoolean(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".onConference"));
		}
		
		log.info("Audio User Settings Read");
	}

	private void store() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			return;
		}
		
		for (Iterator<String> i = audioSettings.getPtuIds().iterator(); i.hasNext();) {
			String ptuId = i.next();
			store.setItem(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".username", audioSettings.getUsername(ptuId));
			store.setItem(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".number", audioSettings.getNumber(ptuId));
			store.setItem(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".channel", audioSettings.getChannel(ptuId));
			store.setItem(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".destUser", audioSettings.getDestUser(ptuId));
			store.setItem(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".destPtu", audioSettings.getDestPtu(ptuId));
			store.setItem(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".status", audioSettings.getStatus(ptuId));
			store.setItem(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".onCall", audioSettings.getOnCall(ptuId));
			store.setItem(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".activity", audioSettings.getActivity(ptuId));
			store.setItem(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".room", audioSettings.getRoom(ptuId));
			store.setItem(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".mute", audioSettings.getMute(ptuId));
			store.setItem(APVS_AUDIO_USERS_SETTINGS + "." + ptuId + ".onConference", audioSettings.getOnConference(ptuId));
		}
	}
}
