package ch.cern.atlas.apvs.daq.server;

import ch.cern.atlas.apvs.domain.Event;

public class TypeEventFilter implements EventFilter {

	@Override
	public boolean filter(Event event) {
		switch (event.getEventType()) {
		case "ValueChange":
			return false;
		default:
			return true;
		}
	}

}
