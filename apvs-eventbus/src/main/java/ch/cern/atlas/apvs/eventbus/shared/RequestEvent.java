package ch.cern.atlas.apvs.eventbus.shared;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class RequestEvent extends Event<RequestEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onRequestEvent(RequestEvent event);
	}

	private static final Type<RequestEvent.Handler> TYPE = new Type<RequestEvent.Handler>();

	/**
	 * Register a handler for events on the eventbus.
	 * 
	 * @param eventBus
	 *            the {@link EventBus}
	 * @param handler
	 *            an Handler instance
	 * @return an {@link HandlerRegistration} instance
	 */
	public static HandlerRegistration register(Object target, EventBus eventBus,
			RequestEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private String requestedClassName;
	private String targetClassName;

	public RequestEvent() {
	}

	public RequestEvent(Class<? extends Event<?>> requestedClass, Class<?> targetClass) {
		this.requestedClassName = requestedClass.getName();
		this.targetClassName = targetClass.getName();
	}

	@Override
	public Type<RequestEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public String getRequestedClassName() {
		return requestedClassName;
	}

	public String getTargetClassName() {
		return targetClassName;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onRequestEvent(this);
	}

	@Override
	public String toString() {
		return "RequestEvent of class "+getRequestedClassName() +" by "+getTargetClassName();
	}
}
