package ch.cern.atlas.apvs.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class NamedEventBus {

	private static Map<String, EventBus> busses;
	
	static {
		busses = new HashMap<String, EventBus>();
		busses.put("", new DummyEventBus());
	}

	public static EventBus get(String name) {
		EventBus bus = busses.get(name);
		if (bus == null) {
			bus = new SimpleEventBus();
			busses.put(name, bus);
		}
		return bus;
	}
	
}
