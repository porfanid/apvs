package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.user.client.rpc.IsSerializable;


//NOTE: implements IsSerializable in case serialization file cannot be found
public class InterventionMap implements Serializable, IsSerializable {

	private static final long serialVersionUID = 8868971785801918119L;

	private Map<Device, Intervention> interventions = new HashMap<Device, Intervention>();
	
	public InterventionMap() {
		// serializable
	}
	
	// For Audio...
	public Intervention get(String name) {
		for (Device device : interventions.keySet()) {
			if (device.getName().equals(name)) {
				return get(device);
			}
		}
		return null;
	}
	
	public Intervention get(Device device) {
		return interventions.get(device);
	}
	
	public Intervention put(Device device, Intervention intervention) {
		return interventions.put(device, intervention);
	}

	public List<Device> getPtus() {
		List<Device> ptus = new ArrayList<Device>(interventions.keySet());
		Collections.sort(ptus);
		return ptus;
	}

	public void clear() {
		interventions.clear();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InterventionMap) {
			//System.out.println("Result: " + this.interventions.entrySet().equals(((InterventionMap)obj).interventions.entrySet()));
			//System.out.println("Function: " + this.equalInterventionMap((InterventionMap) obj));
			//return this.interventions.entrySet().equals(((InterventionMap)obj).interventions.entrySet());
			return this.equalInterventionMap((InterventionMap) obj);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return interventions.entrySet().hashCode();
	}
	
	public boolean equalInterventionMap(InterventionMap obj){
		Iterator<Entry<Device, Intervention>> entries_it = interventions.entrySet().iterator();
		
		List<Intervention> interventionList = new ArrayList<Intervention>();
		List<Intervention> objList = new ArrayList<Intervention>();
		
		while (entries_it.hasNext()) {
			  Entry<Device, Intervention> thisEntry = entries_it.next();
			  Intervention value = thisEntry.getValue();
			  interventionList.add(value);
		}
		
		entries_it = obj.interventions.entrySet().iterator();
		
		while (entries_it.hasNext()) {
			  Entry<Device, Intervention> thisEntry = entries_it.next();
			  Intervention value = thisEntry.getValue();
			  objList.add(value);
		}
		
		if(objList.size() != interventionList.size()){
			return false;
		}

		for(int i=0; i< objList.size(); i++){
			
			//Description test
			if((objList.get(i).getDescription()==null && interventionList.get(i).getDescription()==null))
				;
			else if (objList.get(i).getDescription().equals(interventionList.get(i).getDescription()))
					;
				else{
					System.out.println("Description field changed");
					return false;
				}
			
			//DeviceId test
			if((objList.get(i).getDevice()==null && interventionList.get(i).getDevice()==null))
				;
			else if (objList.get(i).getDevice().equals(interventionList.get(i).getDevice()))
					;
				else{
					System.out.println("Device field changed");
					return false;
				}
				
			//End Time test
			if((objList.get(i).getEndTime()==null && interventionList.get(i).getEndTime()==null))	
					;
			else if (objList.get(i).getEndTime().equals(interventionList.get(i).getEndTime()))
						;
				else{
					System.out.println("End Time field changed");
					return false;
				}
			
			
			
			//Start Time test
			if((objList.get(i).getStartTime()==null && interventionList.get(i).getStartTime()==null))
					;
			else if (objList.get(i).getStartTime().equals(interventionList.get(i).getStartTime()))
					;
				else{
					System.out.println("Start Time field changed");
					return false;
				}
			
			
			//ID test
			if((objList.get(i).getId() == interventionList.get(i).getId()))
				;
			else{
				System.out.println("ID field changed");
				return false;
				}
			
			
			
			//Impact Number test
			if((objList.get(i).getImpactNumber()==null && interventionList.get(i).getImpactNumber()==null))
					;
			else if (objList.get(i).getImpactNumber().equals(interventionList.get(i).getImpactNumber()))
						;
				else{
					System.out.println("Impact Number field changed");
					return false;
				}
				
			
			//Name test
			if((objList.get(i).getName()==null && interventionList.get(i).getName()==null))
					;
			else if (objList.get(i).getName().equals(interventionList.get(i).getName()))
						;
				else{
					System.out.println("Name field changed");
					return false;
				}
				
			
			//PTU test
			if((objList.get(i).getPtuId()==null && interventionList.get(i).getPtuId()==null))
					;
			else if (objList.get(i).getPtuId().equals(interventionList.get(i).getPtuId()))
						;
				else{
					System.out.println("PTU ID field changed");
					return false;
				}
				
			
			//REC Status test
			if((objList.get(i).getRecStatus()==null && interventionList.get(i).getRecStatus()==null))
				;
			else if (objList.get(i).getRecStatus().equals(interventionList.get(i).getRecStatus()))
					;
				else{
					System.out.println("REC Status field changed");
					return false;
				}
				
			//User test
			if((objList.get(i).getUser()==null && interventionList.get(i).getUser()==null))
					;
			else if (objList.get(i).getUser().equals(interventionList.get(i).getUser()))
						;
				else{
					System.out.println("USER  field changed");
					return false;
				}
			
		}
		
		return true;
	}

}
