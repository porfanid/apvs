package ch.cern.atlas.apvs.ptu.shared;

import ch.cern.atlas.apvs.domain.MeasurementConfiguration;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class MeasurementConfigurationChangedEvent extends RemoteEvent<MeasurementConfigurationChangedEvent.Handler> {

	private static final long serialVersionUID = 3868966512164551424L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 */
		void onMeasurementConfigurationChanged(MeasurementConfigurationChangedEvent event);
	}

	private static final Type<MeasurementConfigurationChangedEvent.Handler> TYPE = new Type<MeasurementConfigurationChangedEvent.Handler>();

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
			MeasurementConfigurationChangedEvent.Handler handler) {
		return ((RemoteEventBus)eventBus).addHandler(TYPE, handler);
	}
		
	private MeasurementConfiguration configuration;
	
	public MeasurementConfigurationChangedEvent() {
	}

	public MeasurementConfigurationChangedEvent(MeasurementConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public Type<MeasurementConfigurationChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public MeasurementConfiguration getMeasurementConfiguration() {
		return configuration;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onMeasurementConfigurationChanged(this);
	}
	
	@Override
	public String toString() {
		return "MeasurementConfigurationChangedEvent "+(configuration != null ? configuration.getDevice().getName()+" "+configuration.getSensor() : "null");
	}
}
