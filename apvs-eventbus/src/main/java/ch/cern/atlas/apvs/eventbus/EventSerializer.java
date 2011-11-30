package ch.cern.atlas.apvs.eventbus;

import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.SerialTypes;

/**
 * 
 * @author Mark Donszelmann
 */
@SerialTypes(value = { RemoteEvent.class })
public abstract class EventSerializer extends AtmosphereGWTSerializer {
}
