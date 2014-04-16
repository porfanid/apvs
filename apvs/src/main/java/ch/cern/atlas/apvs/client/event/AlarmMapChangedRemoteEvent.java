package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.domain.AlarmMap;
import ch.cern.atlas.apvs.client.manager.AlarmManager;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class AlarmMapChangedRemoteEvent extends RemoteEvent<AlarmMapChangedRemoteEvent.Handler> {

	private static final long serialVersionUID = 1241126894489671508L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onAlarmMapChanged(AlarmMapChangedRemoteEvent event);
	}

	private static final Type<AlarmMapChangedRemoteEvent.Handler> TYPE = new Type<AlarmMapChangedRemoteEvent.Handler>();

	public static void fire(RemoteEventBus eventBus, AlarmMap alarmMap) throws SerializationException {
		eventBus.fireEvent(new AlarmMapChangedRemoteEvent(alarmMap));
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
			AlarmMapChangedRemoteEvent.Handler handler) {
		try {
			AlarmManager.getInstance(eventBus);
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(Object src, RemoteEventBus eventBus,
			AlarmMapChangedRemoteEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(AlarmMapChangedRemoteEvent.class, src.getClass()));
		
		return registration;
	}

	private AlarmMap alarmMap;

	public AlarmMapChangedRemoteEvent() {
	}
	
	public AlarmMapChangedRemoteEvent(AlarmMap alarmMap) {
		this.alarmMap = alarmMap;
	}

	@Override
	public Type<AlarmMapChangedRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public AlarmMap getAlarmMap() {
		return alarmMap;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onAlarmMapChanged(this);
	}
	
	@Override
	public String toString() {
		return "AlarmMapChangedEvent "+alarmMap;
	}

}
