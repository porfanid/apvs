package ch.cern.atlas.apvs.eventbus.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class RequestRemoteEvent extends RemoteEvent<RequestRemoteEvent.Handler> {

	private static final long serialVersionUID = 6574015234694309172L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 * @throws SerializationException 
		 */
		void onRequestEvent(RequestRemoteEvent event);
	}

	private static final Type<RequestRemoteEvent.Handler> TYPE = new Type<RequestRemoteEvent.Handler>();

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
			RequestRemoteEvent.Handler handler) {
		return ((RemoteEventBus)eventBus).addHandler(TYPE, handler);
	}

	private String requestedClassName;
	private String targetClassName;

	public RequestRemoteEvent() {
	}

	public RequestRemoteEvent(Class<? extends Serializable> requestedClass, Class<?> targetClass) {
		this.requestedClassName = requestedClass.getName();
		this.targetClassName = targetClass.getName();
	}

	@Override
	public Type<RequestRemoteEvent.Handler> getAssociatedType() {
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
		return "RequestRemoteEvent of class "+getRequestedClassName()+" by "+getTargetClassName();
	}

}
