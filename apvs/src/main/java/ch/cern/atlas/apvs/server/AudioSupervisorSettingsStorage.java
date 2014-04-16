package ch.cern.atlas.apvs.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.event.AudioSupervisorSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.VoipAccount;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

public class AudioSupervisorSettingsStorage {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private static final String APVS_AUDIO_SUPERVISOR_SETTINGS = "APVS.audioSupervisor.settings";
	private static AudioSupervisorSettingsStorage instance;
	private VoipAccount supervisorAccount;

	public AudioSupervisorSettingsStorage(final RemoteEventBus eventBus) {

		load();

		AudioSupervisorSettingsChangedRemoteEvent.register(eventBus, new AudioSupervisorSettingsChangedRemoteEvent.Handler() {
			
			@Override
			public void onAudioSupervisorSettingsChanged(AudioSupervisorSettingsChangedRemoteEvent event) {
				supervisorAccount = event.getSupervisorSettings();
				store();	
			}
		}); 
		
		RequestRemoteEvent.register(this, eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				if (event.getRequestedClassName().equals(AudioSupervisorSettingsChangedRemoteEvent.class.getName())) {
					eventBus.fireEvent(new AudioSupervisorSettingsChangedRemoteEvent(supervisorAccount));
				}
			}
		});

		eventBus.fireEvent(new AudioSupervisorSettingsChangedRemoteEvent(supervisorAccount));
	}

	public static AudioSupervisorSettingsStorage getInstance(RemoteEventBus eventBus) {
		if (instance == null) {
			instance = new AudioSupervisorSettingsStorage(eventBus);
		}
		return instance;
	}

	private void load() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			log.warn("Supervisor Audio Settings will not be stored");
			return;
		}

		String json = store.getString(APVS_AUDIO_SUPERVISOR_SETTINGS);
		
		supervisorAccount = new VoipAccount(true);
		
		supervisorAccount.setUsername(store.getString(APVS_AUDIO_SUPERVISOR_SETTINGS + ".username"));
		supervisorAccount.setAccount(store.getString(APVS_AUDIO_SUPERVISOR_SETTINGS + ".account"));
		supervisorAccount.setChannel(store.getString(APVS_AUDIO_SUPERVISOR_SETTINGS + ".channel"));
		supervisorAccount.setDestUser(store.getString(APVS_AUDIO_SUPERVISOR_SETTINGS + ".destUser"));
		supervisorAccount.setDestPTU(store.getString(APVS_AUDIO_SUPERVISOR_SETTINGS + ".destPTU"));
		supervisorAccount.setStatus(store.getBoolean(APVS_AUDIO_SUPERVISOR_SETTINGS + ".status"));
		supervisorAccount.setOnCall(store.getBoolean(APVS_AUDIO_SUPERVISOR_SETTINGS + ".onCall"));
		supervisorAccount.setActivity(store.getString(APVS_AUDIO_SUPERVISOR_SETTINGS + ".activity"));
		supervisorAccount.setRoom(store.getString(APVS_AUDIO_SUPERVISOR_SETTINGS + ".room"));
		supervisorAccount.setMute(store.getBoolean(APVS_AUDIO_SUPERVISOR_SETTINGS + ".mute"));
		supervisorAccount.setOnConference(store.getBoolean(APVS_AUDIO_SUPERVISOR_SETTINGS + ".onConference"));
		
		log.info("Audio Supervisor Settings Read");
	}

	private void store() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			return;
		}

		store.setItem(APVS_AUDIO_SUPERVISOR_SETTINGS + ".username", supervisorAccount.getUsername());
		store.setItem(APVS_AUDIO_SUPERVISOR_SETTINGS + ".account", supervisorAccount.getAccount());
		store.setItem(APVS_AUDIO_SUPERVISOR_SETTINGS + ".channel", supervisorAccount.getChannel());
		store.setItem(APVS_AUDIO_SUPERVISOR_SETTINGS + ".destUser", supervisorAccount.getDestUser());
		store.setItem(APVS_AUDIO_SUPERVISOR_SETTINGS + ".destPTU", supervisorAccount.getDestPTU());
		store.setItem(APVS_AUDIO_SUPERVISOR_SETTINGS + ".status", supervisorAccount.getStatus());
		store.setItem(APVS_AUDIO_SUPERVISOR_SETTINGS + ".onCall", supervisorAccount.getOnCall());
		store.setItem(APVS_AUDIO_SUPERVISOR_SETTINGS + ".activity", supervisorAccount.getActivity());
		store.setItem(APVS_AUDIO_SUPERVISOR_SETTINGS + ".room", supervisorAccount.getRoom());
		store.setItem(APVS_AUDIO_SUPERVISOR_SETTINGS + ".mute", supervisorAccount.getMute());
		store.setItem(APVS_AUDIO_SUPERVISOR_SETTINGS + ".onConference", supervisorAccount.getOnConference());
	}
}
