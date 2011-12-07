package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.client.Settings;
import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SettingsChangedEvent extends Event<SettingsChangedEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onSettingsChanged(SettingsChangedEvent event);
	}

	private static final Type<SettingsChangedEvent.Handler> TYPE = new Type<SettingsChangedEvent.Handler>();

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
			SettingsChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(EventBus eventBus,
			SettingsChangedEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestEvent(SettingsChangedEvent.class));
		
		return registration;
	}

	private Settings settings;

	public SettingsChangedEvent() {
	}

	public SettingsChangedEvent(Settings settings) {
		this.settings = settings;
	}

	@Override
	public Type<SettingsChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Settings getSettings() {
		return settings;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSettingsChanged(this);
	}

}
