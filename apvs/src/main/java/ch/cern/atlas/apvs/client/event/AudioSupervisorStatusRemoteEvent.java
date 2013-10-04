package ch.cern.atlas.apvs.client.event;

import java.util.List;

import ch.cern.atlas.apvs.client.settings.VoipAccount;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class AudioSupervisorStatusRemoteEvent extends
		RemoteEvent<AudioSupervisorStatusRemoteEvent.Handler> {

	private static final long serialVersionUID = 1L;

	public interface Handler {

		void onAudioSupervisorStatusChanged(AudioSupervisorStatusRemoteEvent event);
	}

	private static final Type<AudioSupervisorStatusRemoteEvent.Handler> TYPE = new Type<AudioSupervisorStatusRemoteEvent.Handler>();

	public static HandlerRegistration register(RemoteEventBus eventBus,
			AudioSupervisorStatusRemoteEvent.Handler handler) {

		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			AudioSupervisorStatusRemoteEvent.Handler handler) {

		HandlerRegistration registration = register(eventBus, handler);
		eventBus.fireEvent(new RequestRemoteEvent(AudioSupervisorStatusRemoteEvent.class));

		return registration;
	}

	private List<VoipAccount> supervisorAccounts;
	

	public AudioSupervisorStatusRemoteEvent() {
	}

	public AudioSupervisorStatusRemoteEvent(List<VoipAccount> supervisorList) {
		this.supervisorAccounts = supervisorList;
	}

	public List<VoipAccount> getSupervisorsList() {
		return supervisorAccounts;
	}

	@Override
	public Type<AudioSupervisorStatusRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onAudioSupervisorStatusChanged(this);
	}

}
