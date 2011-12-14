package ch.cern.atlas.apvs.client.event;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class StepStatusEvent extends Event<StepStatusEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onStepStatus(StepStatusEvent event);
	}

	private static final Type<StepStatusEvent.Handler> TYPE = new Type<StepStatusEvent.Handler>();

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
			StepStatusEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private int step, total;
	private boolean previous, next;
	
	public StepStatusEvent() {
	}

	public StepStatusEvent(int step, int total, boolean hasPrevious, boolean hasNext) {
		this.step = step;
		this.total = total;
		previous = hasPrevious;
		next = hasNext;
	}

	@Override
	public Type<StepStatusEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public int getStep() {
		return step;
	}
	
	public int getTotal() {
		return total;
	}
	
	public boolean hasPrevious() {
		return previous;
	}
	
	public boolean hasNext() {
		return next;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onStepStatus(this);
	}
}
