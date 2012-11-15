package ch.cern.atlas.apvs.client.event;

import java.util.List;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.HandlerRegistration;

public class AsteriskStatusRemoteEvent extends
		RemoteEvent<AsteriskStatusRemoteEvent.Handler> {

	private static final long serialVersionUID = 1L;

	public interface Handler {

		void onAsteriskStatusChange(AsteriskStatusRemoteEvent event);
	}

	private static final Type<AsteriskStatusRemoteEvent.Handler> TYPE = new Type<AsteriskStatusRemoteEvent.Handler>();

	public static HandlerRegistration register(RemoteEventBus eventBus,
			AsteriskStatusRemoteEvent.Handler handler) {

		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			AsteriskStatusRemoteEvent.Handler handler) {

		HandlerRegistration registration = register(eventBus, handler);
		eventBus.fireEvent(new RequestRemoteEvent(AsteriskStatusRemoteEvent.class));

		return registration;
	}

	private List<String> usersList;

	public AsteriskStatusRemoteEvent() {
	}

	public AsteriskStatusRemoteEvent(List<String> usersList) {
		this.usersList = usersList;
	}

	public List<String> getAsteriskUsersList() {
		return usersList;
	}

	@Override
	public Type<AsteriskStatusRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AsteriskStatusRemoteEvent.Handler handler) {
		handler.onAsteriskStatusChange(this);

	}

}
