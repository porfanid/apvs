package ch.cern.atlas.apvs.daq.server;

import java.util.List;
  
import ch.cern.atlas.apvs.domain.Measurement;

public class ValueFilter implements Filter {

	@Override
	public boolean filter(Measurement current, List<Measurement> list,
			double resolution) {
		// TODO Auto-generated method stub
		int lengthOfList=list.size();
		System.out.println("ValueFilter initial lengthOfList = " + lengthOfList);
		boolean res;
		if (lengthOfList < 2){
			//with 0 or 1 previous measurement in LIST, just add current as new element of list.
			list.add(current);
			//return false
			res = false;
		} else{
			Measurement lastMeasurement = list.get(lengthOfList-1);
			Measurement secondLastMeasurement = list.get(lengthOfList-2);
			double currentValue = current.getValue();
			if ((!isInThreshold(lastMeasurement.getValue(), currentValue, resolution)) || 
			   (!isInThreshold(secondLastMeasurement.getValue(), lastMeasurement.getValue(), resolution))){
				// current value is over resolution-distance from last measurement or from second last measurement: 
				// it has to be added as new measurement
				System.out.println("ValueFilter: add <"+current.getValue()+"> to the list");
			    list.add(current);
			    // return false;
			    res = false;
			} else {
				// current measurement is inside the resolution-distance from last and second last measurements: 
				// update the timestamp
				lastMeasurement.setDate(current.getDate());
				//return true;
				res = true;
			}
		}
		return res;
	}
	
	private static boolean isInThreshold(double v1, double v2, double distance){
		return Math.abs(v1-v2)<=distance;
	}
}