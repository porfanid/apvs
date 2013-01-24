package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.domain.AlarmMap;
import ch.cern.atlas.apvs.client.manager.AlarmManager;
import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class AlarmMapChangedEvent extends Event<AlarmMapChangedEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onHistoryMapChanged(AlarmMapChangedEvent event);
	}

	private static final Type<AlarmMapChangedEvent.Handler> TYPE = new Type<AlarmMapChangedEvent.Handler>();

	public static void fire(EventBus eventBus, AlarmMap alarmMap) {
		eventBus.fireEvent(new AlarmMapChangedEvent(alarmMap));
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
			AlarmMapChangedEvent.Handler handler) {
		AlarmManager.getInstance(clientFactory.getRemoteEventBus());
		
		return clientFactory.getRemoteEventBus().addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(ClientFactory clientFactory,
			AlarmMapChangedEvent.Handler handler) {
		HandlerRegistration registration = register(clientFactory, handler);
		
		clientFactory.getRemoteEventBus().fireEvent(new RequestEvent(AlarmMapChangedEvent.class));
		
		return registration;
	}

	private AlarmMap alarmMap;

	public AlarmMapChangedEvent() {
	}
	
	public AlarmMapChangedEvent(AlarmMap alarmMap) {
		this.alarmMap = alarmMap;
	}

	@Override
	public Type<AlarmMapChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public AlarmMap getAlarmMap() {
		return alarmMap;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onHistoryMapChanged(this);
	}
	
	@Override
	public String toString() {
		return "AlarmMapChangedEvent "+alarmMap;
	}

}
