package ch.cern.atlas.apvs.client.event;

import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.SerialTypes;

@SerialTypes(ServerSettingsChangedRemoteEvent.class)
public abstract class EventSerializer extends AtmosphereGWTSerializer {
}