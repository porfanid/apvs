package ch.cern.atlas.apvs.client;

import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.SerialTypes;

import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.places.RemotePlace;
import ch.cern.atlas.apvs.client.places.User;

/**
 * 
 * @author Mark Donszelmann
 */
@SerialTypes(value = { RemotePlace.class, User.class, SelectPtuEvent.class })
public abstract class EventSerializer extends AtmosphereGWTSerializer {
}
