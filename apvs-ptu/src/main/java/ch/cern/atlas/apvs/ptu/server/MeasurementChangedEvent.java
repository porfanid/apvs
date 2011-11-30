package ch.cern.atlas.apvs.ptu.server;

import ch.cern.atlas.apvs.domain.Measurement;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class MeasurementChangedEvent extends Event<MeasurementChangedEvent.Handler> {

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onMeasurementChanged(MeasurementChangedEvent event);
	}

	private static final Type<MeasurementChangedEvent.Handler> TYPE = new Type<MeasurementChangedEvent.Handler>();

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
			MeasurementChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	private final Measurement<?> measurement;

	public MeasurementChangedEvent(Measurement<?> measurement) {
		this.measurement = measurement;
	}

	@Override
	public Type<MeasurementChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Measurement<?> getMeasurement() {
		return measurement;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onMeasurementChanged(this);
	}
}
