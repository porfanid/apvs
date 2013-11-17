package ch.cern.atlas.apvs.daq.server;

import ch.cern.atlas.apvs.domain.Event;

public interface EventFilter {

	/**
	 * Filter the current event
	 * 
	 * @return true ok
	 */
	boolean filter(Event event);
}