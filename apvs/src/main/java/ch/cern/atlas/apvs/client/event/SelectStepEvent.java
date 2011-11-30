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
	public static HandlerRegistration register(EventBus eventBus,
			SelectStepEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private final int step, total;
	private final boolean previous, next;

	public SelectStepEvent(int step, int total, boolean hasPrevious, boolean hasNext) {
		this.step = step;
		this.total = total;
		previous = hasPrevious;
		next = hasNext;
	}

	@Override
	public Type<SelectStepEvent.Handler> getAssociatedType() {
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
		handler.onStepSelected(this);
	}
}
