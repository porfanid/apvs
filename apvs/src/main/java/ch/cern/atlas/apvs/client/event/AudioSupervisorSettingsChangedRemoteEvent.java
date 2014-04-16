package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.settings.VoipAccount;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.HandlerRegistration;

public class AudioSupervisorSettingsChangedRemoteEvent extends
		RemoteEvent<AudioSupervisorSettingsChangedRemoteEvent.Handler> {

	private static final long serialVersionUID = 1L;

	public interface Handler {

		void onAudioSupervisorSettingsChanged(AudioSupervisorSettingsChangedRemoteEvent event);
	}

	private static final Type<AudioSupervisorSettingsChangedRemoteEvent.Handler> TYPE = new Type<AudioSupervisorSettingsChangedRemoteEvent.Handler>();

	public static HandlerRegistration register(RemoteEventBus eventBus,
			AudioSupervisorSettingsChangedRemoteEvent.Handler handler) {

		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(Object src, RemoteEventBus eventBus,
			AudioSupervisorSettingsChangedRemoteEvent.Handler handler) {

		HandlerRegistration registration = register(eventBus, handler);
		eventBus.fireEvent(new RequestRemoteEvent(AudioSupervisorSettingsChangedRemoteEvent.class, src.getClass()));

		return registration;
	}

	private VoipAccount supervisor;

	public AudioSupervisorSettingsChangedRemoteEvent() {
	}

	public AudioSupervisorSettingsChangedRemoteEvent(VoipAccount voipAccount) {
		this.supervisor = voipAccount;
	}

	public VoipAccount getSupervisorSettings() {
		return supervisor;
	}

	@Override
	public Type<AudioSupervisorSettingsChangedRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onAudioSupervisorSettingsChanged(this);
	}

}
