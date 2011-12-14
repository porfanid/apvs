package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectStepEvent extends RemoteEvent<SelectStepEvent.Handler> {

	private static final long serialVersionUID = 8083231975565002912L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onStepSelected(SelectStepEvent event);
	}

	private static final Type<SelectStepEvent.Handler> TYPE = new Type<SelectStepEvent.Handler>();

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
			SelectStepEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private int step;
	
	public SelectStepEvent() {
	}

	public SelectStepEvent(int step) {
		this.step = step;
	}

	@Override
	public Type<SelectStepEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public int getStep() {
		return step;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onStepSelected(this);
	}
}
