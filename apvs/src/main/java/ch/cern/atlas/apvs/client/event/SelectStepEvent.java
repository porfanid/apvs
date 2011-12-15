package ch.cern.atlas.apvs.client.event;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class SelectStepEvent extends Event<SelectStepEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onSelectStep(SelectStepEvent event);
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
	public static HandlerRegistration register(EventBus eventBus,
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
		handler.onSelectStep(this);
	}
}
