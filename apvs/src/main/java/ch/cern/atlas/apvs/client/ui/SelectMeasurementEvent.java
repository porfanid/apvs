package ch.cern.atlas.apvs.client.ui;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectMeasurementEvent extends Event<SelectMeasurementEvent.Handler> {

	public interface Handler {
		void onSelection(SelectMeasurementEvent event);
	}

	private static final Type<SelectMeasurementEvent.Handler> TYPE = new Type<SelectMeasurementEvent.Handler>();

	public static void fire(EventBus eventBus, String sourceName, String name) {
		eventBus.fireEventFromSource(new SelectMeasurementEvent(name), sourceName);
	}

	public static HandlerRegistration register(EventBus eventBus,
			String sourceName, Handler handler) {
		return eventBus.addHandlerToSource(TYPE, sourceName, handler);
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
