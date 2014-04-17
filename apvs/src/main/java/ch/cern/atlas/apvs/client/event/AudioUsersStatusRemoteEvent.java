package ch.cern.atlas.apvs.client.event;

import java.util.List;

import ch.cern.atlas.apvs.client.settings.VoipAccount;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class AudioUsersStatusRemoteEvent extends
		RemoteEvent<AudioUsersStatusRemoteEvent.Handler> {

	private static final long serialVersionUID = 1L;

	public interface Handler {

		void onAudioUsersStatusChange(AudioUsersStatusRemoteEvent event);
	}

	private static final Type<AudioUsersStatusRemoteEvent.Handler> TYPE = new Type<AudioUsersStatusRemoteEvent.Handler>();

	public static HandlerRegistration register(RemoteEventBus eventBus,
			AudioUsersStatusRemoteEvent.Handler handler) {

		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(Object src, RemoteEventBus eventBus,
			AudioUsersStatusRemoteEvent.Handler handler) throws SerializationException {

		HandlerRegistration registration = register(eventBus, handler);
		eventBus.fireEvent(new RequestRemoteEvent(src, AudioUsersStatusRemoteEvent.class));

		return registration;
	}

	private List<VoipAccount> usersList;

	public AudioUsersStatusRemoteEvent() {
	}

	public AudioUsersStatusRemoteEvent(List<VoipAccount> usersList) {
		this.usersList = usersList;
	}

	public List<VoipAccount> getUsersList() {
		return usersList;
	}

	@Override
	public Type<AudioUsersStatusRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AudioUsersStatusRemoteEvent.Handler handler) {
		handler.onAudioUsersStatusChange(this);

	}

}
