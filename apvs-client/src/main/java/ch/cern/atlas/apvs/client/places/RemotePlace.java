package ch.cern.atlas.apvs.client.places;

import java.io.Serializable;

import com.google.gwt.place.shared.Place;

@SuppressWarnings("serial")
public abstract class RemotePlace extends Place implements Serializable {

	private int remoteId;
	
	public RemotePlace() {
	}
	
	protected RemotePlace(int remoteId) {
		this.remoteId = remoteId;
	}
	
	public int getRemoteID() {
		return remoteId;
	}
	
	public String toString() {
		return "RemoteID: "+getRemoteID();
	}
}
