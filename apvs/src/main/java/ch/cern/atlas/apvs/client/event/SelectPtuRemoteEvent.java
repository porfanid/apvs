package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectPtuRemoteEvent extends RemoteEvent<SelectPtuRemoteEvent.Handler> {

	private static final long serialVersionUID = 4782769296921555320L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onPtuSelected(SelectPtuRemoteEvent event);
	}

	private static final Type<SelectPtuRemoteEvent.Handler> TYPE = new Type<SelectPtuRemoteEvent.Handler>();

	/**
	 * Register a handler for events on the eventbus.
	 * 
	 * @param eventBus
	 *            the {@link EventBus}
	 * @param handler
	 *            an Handler instance
	 * @return an {@link HandlerRegistration} instance
	 */
	public static HandlerRegistration register(EventBus eventBus,
			SelectPtuRemoteEvent.Handler handler) {
		return ((RemoteEventBus)eventBus).addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(EventBus eventBus,
			SelectPtuRemoteEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		((RemoteEventBus)eventBus).fireEvent(new RequestRemoteEvent(SelectPtuRemoteEvent.class));
		
		return registration;
	}

	private Integer ptuId;

	public SelectPtuRemoteEvent() {
	}
	
	public SelectPtuRemoteEvent(Integer ptuId) {
		this.ptuId = ptuId;
	}

	@Override
	public Type<SelectPtuRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Integer getPtuId() {
		return ptuId;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onPtuSelected(this);
	}
	
	@Override
	public String toString() {
		return "SelectPtuRemoteEvent "+ptuId;
	}

}
