package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.settings.InterventionMap;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class InterventionMapChangedEvent extends RemoteEvent<InterventionMapChangedEvent.Handler> {

	private static final long serialVersionUID = -6369043960585044786L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onInterventionMapChanged(InterventionMapChangedEvent event);
	}

	public static void fire(RemoteEventBus eventBus, InterventionMap interventions) {
		eventBus.fireEvent(new InterventionMapChangedEvent(interventions));
	}	
	
	private static final Type<InterventionMapChangedEvent.Handler> TYPE = new Type<InterventionMapChangedEvent.Handler>();

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
			InterventionMapChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			InterventionMapChangedEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(InterventionMapChangedEvent.class));
		
		return registration;
	}

	private InterventionMap interventions;

	public InterventionMapChangedEvent() {
	}

	public InterventionMapChangedEvent(InterventionMap interventions) {
		this.interventions = interventions;
	}

	@Override
	public Type<InterventionMapChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public InterventionMap getInterventionMap() {
		return interventions;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onInterventionMapChanged(this);
	}

}
