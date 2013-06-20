package ch.cern.atlas.apvs.client.places;

import java.io.Serializable;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
@SuppressWarnings("serial")
public abstract class SharedPlace extends Place implements Serializable, IsSerializable {

	public SharedPlace() {
	}
}
