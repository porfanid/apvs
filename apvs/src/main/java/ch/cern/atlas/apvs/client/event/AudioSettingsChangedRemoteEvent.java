package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.HandlerRegistration;

public class AudioSettingsChangedRemoteEvent extends
		RemoteEvent<AudioSettingsChangedRemoteEvent.Handler> {

	private static final long serialVersionUID = 1L;

	public interface Handler {

		void onAudioSettingsChanged(AudioSettingsChangedRemoteEvent event);
	}

	private static final Type<AudioSettingsChangedRemoteEvent.Handler> TYPE = new Type<AudioSettingsChangedRemoteEvent.Handler>();

	public static HandlerRegistration register(RemoteEventBus eventBus,
			AudioSettingsChangedRemoteEvent.Handler handler) {

		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			AudioSettingsChangedRemoteEvent.Handler handler) {

		HandlerRegistration registration = register(eventBus, handler);
		eventBus.fireEvent(new RequestRemoteEvent(AudioSettingsChangedRemoteEvent.class));

		return registration;
	}

	private AudioSettings voipAccounts;

	public AudioSettingsChangedRemoteEvent() {
	}

	public AudioSettingsChangedRemoteEvent(AudioSettings voipAccount) {
		this.voipAccounts = voipAccount;
	}

	public AudioSettings getAudioSettings() {
		return voipAccounts;
	}

	@Override
	public Type<AudioSettingsChangedRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onAudioSettingsChanged(this);
	}

}
