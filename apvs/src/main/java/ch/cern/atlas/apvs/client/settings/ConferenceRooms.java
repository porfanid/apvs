package ch.cern.atlas.apvs.client.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

import ch.cern.atlas.apvs.client.domain.Conference;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class ConferenceRooms implements Serializable, IsSerializable {

	private static final long serialVersionUID = 1L;

	private Map<String, Conference> conferenceRooms = new HashMap<String, Conference>();

	public ConferenceRooms() {
	}

	public List<String> getRooms() {
		List<String> rooms = new ArrayList<String>();
		rooms.addAll(conferenceRooms.keySet());
		return rooms;
	}

	public Conference get(String room) {
		return (conferenceRooms.containsKey(room) ? conferenceRooms.get(room)
				: null);
	}

	public Conference put(String room, Conference conference) {
		return (conferenceRooms.put(room, conference));
	}

	public boolean roomExist(String room) {
		return conferenceRooms.containsKey(room);
	}

	public String roomOfActivity(String activity) {
		List<String> roomNumbers = new ArrayList<String>(this.getRooms());
		for (int i = 0; i < roomNumbers.size(); i++) {
			if (this.get(roomNumbers.get(i)).getActivity().equals(activity))
				return roomNumbers.get(i);
		}
		return null;
	}

	public boolean conferenceOfActivityExist(String activity) {
		return (roomOfActivity(activity) != null ? true : false);
	}

	public void remove(String room) {
		conferenceRooms.remove(room);
	}

	public void clear() {
		conferenceRooms.clear();
	}
	
	public String newRoom(){
		for(int i=0; i<10000 ;i++){
			if(!roomExist(Integer.toString(i))){
				return Integer.toString(i);
			}
		}
		return null; 
	}
}