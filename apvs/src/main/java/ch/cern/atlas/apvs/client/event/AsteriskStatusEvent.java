package ch.cern.atlas.apvs.client.event;

import java.util.List;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

public class AsteriskStatusEvent extends RemoteEvent<AsteriskStatusEvent.Handler> {
	
	public interface Handler{
		
		void onAsteriskStatusChange(AsteriskStatusEvent event);
	}

	private static final Type<AsteriskStatusEvent.Handler> TYPE = new Type<AsteriskStatusEvent.Handler>();

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
			AsteriskStatusEvent.Handler handler){
		
				return eventBus.addHandler(TYPE, handler);
	}
	
	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			AsteriskStatusEvent.Handler handler){
		
				HandlerRegistration registration = register(eventBus, handler);
				eventBus.fireEvent(new RequestRemoteEvent(AsteriskStatusEvent.class));
				
				return registration;
	}
	
	private List<String> usersList;
	
	public AsteriskStatusEvent() {
	}
	
	public AsteriskStatusEvent(List<String> usersList){
		this.usersList = usersList;
	}
	
	public List<String> getAsteriskUsersList(){
		return usersList;
	}

	@Override
	public Type<AsteriskStatusEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AsteriskStatusEvent.Handler handler) {
		handler.onAsteriskStatusChange(this);

	}
	

	

}
