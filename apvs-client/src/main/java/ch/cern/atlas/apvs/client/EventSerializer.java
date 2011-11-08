package ch.cern.atlas.apvs.client;

import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.SerialTypes;

/**
 * 
 * @author Mark Donszelmann
 */
@SerialTypes(value = { Event.class, TabSelectEvent.class })
public abstract class EventSerializer extends AtmosphereGWTSerializer {
}
