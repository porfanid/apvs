package ch.cern.atlas.apvs.client.ui;

import java.util.Map;

import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ColorMapChangedEvent extends Event<ColorMapChangedEvent.Handler> {

	public interface Handler {
		void onColorMapChanged(ColorMapChangedEvent event);
	}

	private static final Type<ColorMapChangedEvent.Handler> TYPE = new Type<ColorMapChangedEvent.Handler>();

	public static void fire(EventBus eventBus, Map<String, String> colorMap) {
		eventBus.fireEvent(new ColorMapChangedEvent(colorMap));
	}

	public static HandlerRegistration register(EventBus eventBus,
			Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(EventBus eventBus,
			ColorMapChangedEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestEvent(ColorMapChangedEvent.class));
		
		return registration;
	}
	
	private Map<String, String> colorMap;

	@Override
	public Event.Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	public Map<String, String> getColorMap() {
		return colorMap;
	}

	protected ColorMapChangedEvent(Map<String, String> colorMap) {
		this.colorMap = colorMap;
	}

	@Override
	protected void dispatch(ColorMapChangedEvent.Handler handler) {
		handler.onColorMapChanged(this);
	}

}
