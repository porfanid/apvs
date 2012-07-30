package ch.cern.atlas.apvs.client;

import java.util.HashMap;
import java.util.Map;

import ch.cern.atlas.apvs.client.ui.DummyEventBus;

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
			put(name, bus);
		}
		return bus;
	}
	
	static void put(String name, EventBus bus) {
		busses.put(name,  bus);
	}
	
}
