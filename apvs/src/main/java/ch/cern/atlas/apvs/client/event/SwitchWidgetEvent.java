package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SwitchWidgetEvent extends Event<SwitchWidgetEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onSwitchWidget(SwitchWidgetEvent event);
	}

	private static final Type<SwitchWidgetEvent.Handler> TYPE = new Type<SwitchWidgetEvent.Handler>();

	public static void fire(EventBus eventBus, String title, Widget widget, boolean replacement) {
		eventBus.fireEvent(new SwitchWidgetEvent(title, widget, replacement));
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
	public static HandlerRegistration register(EventBus eventBus,
			SwitchWidgetEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(EventBus eventBus,
			SwitchWidgetEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestEvent(SwitchWidgetEvent.class));
		
		return registration;
	}

	private String title;
	private Widget widget;
	private boolean replacement;

	public SwitchWidgetEvent() {
	}
	
	public SwitchWidgetEvent(String title, Widget widget, boolean replacement) {
		this.title = title;
		this.widget = widget;
		this.replacement = replacement;
	}

	@Override
	public Type<SwitchWidgetEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public String getTitle() {
		return title;
	}
	
	public Widget getWidget() {
		return widget;
	}
	
	public boolean isReplacement() {
		return replacement;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onSwitchWidget(this);
	}
	
	@Override
	public String toString() {
		return "SwitchWidget "+title+" "+widget;
	}
}
