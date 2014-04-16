package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.HandlerRegistration;

public class AudioUsersSettingsChangedRemoteEvent extends
		RemoteEvent<AudioUsersSettingsChangedRemoteEvent.Handler> {

	private static final long serialVersionUID = 1L;

	public interface Handler {

		void onAudioUsersSettingsChanged(AudioUsersSettingsChangedRemoteEvent event);
	}

	private static final Type<AudioUsersSettingsChangedRemoteEvent.Handler> TYPE = new Type<AudioUsersSettingsChangedRemoteEvent.Handler>();

	public static HandlerRegistration register(RemoteEventBus eventBus,
			AudioUsersSettingsChangedRemoteEvent.Handler handler) {

		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(Object src, RemoteEventBus eventBus,
			AudioUsersSettingsChangedRemoteEvent.Handler handler) {

		HandlerRegistration registration = register(eventBus, handler);
		eventBus.fireEvent(new RequestRemoteEvent(AudioUsersSettingsChangedRemoteEvent.class, src.getClass()));

		return registration;
	}

	private AudioSettings voipAccounts;

	public AudioUsersSettingsChangedRemoteEvent() {
	}

	public AudioUsersSettingsChangedRemoteEvent(AudioSettings voipAccount) {
		this.voipAccounts = voipAccount;
	}

	public AudioSettings getAudioSettings() {
		return voipAccounts;
	}

	@Override
	public Type<AudioUsersSettingsChangedRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onAudioUsersSettingsChanged(this);
	}

}
