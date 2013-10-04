package ch.cern.atlas.apvs.client.event;

import org.atmosphere.gwt20.client.GwtRpcClientSerializer;
import org.atmosphere.gwt20.client.GwtRpcSerialTypes;


@GwtRpcSerialTypes(ServerSettingsChangedRemoteEvent.class)
public abstract class EventSerializer extends GwtRpcClientSerializer {
}