package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.manager.HistoryManager;
import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class HistoryChangedEvent extends Event<HistoryChangedEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onHistoryChanged(HistoryChangedEvent event);
	}

	private static final Type<HistoryChangedEvent.Handler> TYPE = new Type<HistoryChangedEvent.Handler>();

	public static void fire(EventBus eventBus, History history) {
		eventBus.fireEvent(new HistoryChangedEvent(history));
	}	
	
	/**
	 * Register a handler for events on the eventbus.
	 * 
	 * @param eventBus
	 *            the {@link EventBus}
	 * @param handler
	 *            an Handler instance
	 * @return an {@link HandlerRegistration} instance
	 */
	public static HandlerRegistration register(ClientFactory clientFactory,
			HistoryChangedEvent.Handler handler) {
		HistoryManager.getInstance(clientFactory);
		
		return clientFactory.getRemoteEventBus().addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(ClientFactory clientFactory,
			HistoryChangedEvent.Handler handler) {
		HandlerRegistration registration = register(clientFactory, handler);
		
		clientFactory.getRemoteEventBus().fireEvent(new RequestEvent(HistoryChangedEvent.class));
		
		return registration;
	}

	private History history;

	public HistoryChangedEvent() {
	}
	
	public HistoryChangedEvent(History history) {
		this.history = history;
	}

	@Override
	public Type<HistoryChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public History getHistory() {
		return history;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onHistoryChanged(this);
	}
	
	@Override
	public String toString() {
		return "HistoryChangedEvent "+history;
	}

}
