package ch.cern.atlas.apvs.client;

import java.io.Serializable;

import ch.cern.atlas.apvs.client.places.RemotePlace;

public class RemotePlaceChangeEvent implements Serializable {

	private static final long serialVersionUID = -937798035900759094L;
	private RemotePlace place;
	
	public RemotePlaceChangeEvent() {
	}
	
	public RemotePlaceChangeEvent(RemotePlace place) {
		this.place = place;
	} 
	
	public String toString() {
		return place.toString();
	}
}
