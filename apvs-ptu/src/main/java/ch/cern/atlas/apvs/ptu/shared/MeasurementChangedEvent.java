package ch.cern.atlas.apvs.ptu.shared;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class MeasurementChangedEvent extends RemoteEvent<MeasurementChangedEvent.Handler> {

	private static final long serialVersionUID = 8888428241134416295L;

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
		return ((RemoteEventBus)eventBus).addHandler(TYPE, handler);
	}	
	
	private Measurement measurement;
	
	public MeasurementChangedEvent() {
	}

	public MeasurementChangedEvent(Measurement measurement) {
		this.measurement = measurement;
	}

	@Override
	public Type<MeasurementChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Measurement getMeasurement() {
		return measurement;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onMeasurementChanged(this);
	}
	
	@Override
	public String toString() {
		return "MeasurementChangedEvent "+measurement.getPtuId()+" "+measurement.getName();
	}
}
