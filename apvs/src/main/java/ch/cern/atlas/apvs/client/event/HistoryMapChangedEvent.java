package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.domain.HistoryMap;
import ch.cern.atlas.apvs.client.manager.HistoryManager;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class HistoryMapChangedEvent extends Event<HistoryMapChangedEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onHistoryMapChanged(HistoryMapChangedEvent event);
	}

	private static final Type<HistoryMapChangedEvent.Handler> TYPE = new Type<HistoryMapChangedEvent.Handler>();

	public static void fire(EventBus eventBus, HistoryMap historyMap) {
		eventBus.fireEvent(new HistoryMapChangedEvent(historyMap));
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
	public static HandlerRegistration register(RemoteEventBus eventBus,
			HistoryMapChangedEvent.Handler handler) {
		HistoryManager.getInstance(eventBus);
		
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			HistoryMapChangedEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestEvent(HistoryMapChangedEvent.class));
		
		return registration;
	}

	private HistoryMap historyMap;

	public HistoryMapChangedEvent() {
	}
	
	public HistoryMapChangedEvent(HistoryMap historyMap) {
		this.historyMap = historyMap;
	}

	@Override
	public Type<HistoryMapChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public HistoryMap getHistoryMap() {
		return historyMap;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onHistoryMapChanged(this);
	}
	
	@Override
	public String toString() {
		return "HistoryMapChangedEvent "+historyMap;
	}

}
