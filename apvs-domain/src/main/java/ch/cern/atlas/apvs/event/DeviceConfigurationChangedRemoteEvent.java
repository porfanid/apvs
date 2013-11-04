package ch.cern.atlas.apvs.event;

import ch.cern.atlas.apvs.domain.DeviceConfiguration;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class DeviceConfigurationChangedRemoteEvent extends RemoteEvent<DeviceConfigurationChangedRemoteEvent.Handler> {

	private static final long serialVersionUID = -3804415381754566951L;

	public interface Handler {
		/**
		 * Called when an event is fired.
		 * 
		 * @param event
		 *            an {@link MessageReceivedEvent} instance
		 * @throws SerializationException 
		 */
		void onDeviceConfigurationChanged(DeviceConfigurationChangedRemoteEvent event);
	}

	public static void fire(RemoteEventBus eventBus, DeviceConfiguration deviceConfiguration) {
		eventBus.fireEvent(new DeviceConfigurationChangedRemoteEvent(deviceConfiguration));
	}	
	
	private static final Type<DeviceConfigurationChangedRemoteEvent.Handler> TYPE = new Type<DeviceConfigurationChangedRemoteEvent.Handler>();

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
			DeviceConfigurationChangedRemoteEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public static HandlerRegistration subscribe(RemoteEventBus eventBus,
			DeviceConfigurationChangedRemoteEvent.Handler handler) {
		HandlerRegistration registration = register(eventBus, handler);
		
		eventBus.fireEvent(new RequestRemoteEvent(DeviceConfigurationChangedRemoteEvent.class));
		
		return registration;
	}

	private DeviceConfiguration deviceConfiguration;

	public DeviceConfigurationChangedRemoteEvent() {
	}

	public DeviceConfigurationChangedRemoteEvent(DeviceConfiguration deviceConfiguration) {
		this.deviceConfiguration = deviceConfiguration;
	}

	@Override
	public Type<DeviceConfigurationChangedRemoteEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public DeviceConfiguration getDeviceConfiguration() {
		return deviceConfiguration;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onDeviceConfigurationChanged(this);
	}

}
