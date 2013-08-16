package ch.cern.atlas.apvs.daq.server;

import java.util.List;

import ch.cern.atlas.apvs.domain.Measurement;

public interface Filter {

	/**
	 * Filter the current measurement, by checking the list of previous measurements with regards to the resolution. 
	 * If the current measurement is discarded, false is returned and no changes are made to the list.
	 * If the current measurement is accepted, false is returned BUT the current measurement is added to the list. 
	 * If the current measurement is just a change to the time/connection status of the last measurement, the last
	 * entry in the list is changed, and true is returned. 
	 * 
	 * @param current the current measurement which may need to be filtered.
	 * @param list the previous measurements in a list. The list may contain any number of entries. 
	 * @param resolution a number to be used in the filter calculation
	 * @return true if the current measurement made just an update to the list, false if the current measurement was discarded or added to the list
	 * (the new size of the list will show if the measurement was added).
	 */
	boolean filter(Measurement current, List<Measurement> list, double resolution);
}