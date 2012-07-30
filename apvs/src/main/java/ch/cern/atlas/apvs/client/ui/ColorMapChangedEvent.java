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

	public static void fire(EventBus eventBus, Map<Integer, String> colorMap) {
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
	
	private Map<Integer, String> colorMap;

	@Override
	public Event.Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	public Map<Integer, String> getColorMap() {
		return colorMap;
	}

	protected ColorMapChangedEvent(Map<Integer, String> colorMap) {
		this.colorMap = colorMap;
	}

	@Override
	protected void dispatch(ColorMapChangedEvent.Handler handler) {
		handler.onColorMapChanged(this);
	}

}
