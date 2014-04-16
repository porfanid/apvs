package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectMeasurementEvent extends Event<SelectMeasurementEvent.Handler> {

	public interface Handler {
		void onSelection(SelectMeasurementEvent event);
	}

	private static final Type<SelectMeasurementEvent.Handler> TYPE = new Type<SelectMeasurementEvent.Handler>();

	public static void fire(EventBus eventBus, String name) {
		eventBus.fireEvent(new SelectMeasurementEvent(name));
	}

	public static HandlerRegistration register(EventBus eventBus, Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	public static HandlerRegistration subscribe(Object target, EventBus eventBus,
			SelectMeasurementEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestEvent(SelectMeasurementEvent.class, target.getClass()));
		
		return registration;
	}


	private String name;

	@Override
	public Event.Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	public String getName() {
		return name;
	}

	protected SelectMeasurementEvent(String name) {
		this.name = name;
	}

	@Override
	protected void dispatch(SelectMeasurementEvent.Handler handler) {
		handler.onSelection(this);
	}
}
