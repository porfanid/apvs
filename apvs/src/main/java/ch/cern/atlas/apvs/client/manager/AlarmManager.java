package ch.cern.atlas.apvs.client.manager;

import java.util.List;

import ch.cern.atlas.apvs.client.domain.AlarmMap;
import ch.cern.atlas.apvs.client.event.AlarmMapChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.EventChangedEvent;

public class AlarmManager {

	private static AlarmManager instance;
	private AlarmMap alarms;
	private List<String> ptuIds;
	private RemoteEventBus eventBus;

	private AlarmManager(RemoteEventBus eventBus) {
		this.eventBus = eventBus;
		
		alarms = new AlarmMap();
		
		// subscribe
		InterventionMapChangedRemoteEvent.subscribe(eventBus,
				new InterventionMapChangedRemoteEvent.Handler() {

					@Override
					public void onInterventionMapChanged(
							InterventionMapChangedRemoteEvent event) {
						// FIXME #298
						System.err
								.println("FIXME (3x?)... Received............."
										+ event.getEventBusUUID() + " "
										+ event.getSourceUUID());
						List<String> newPtuIds = event.getInterventionMap()
								.getPtuIds();
						if (!newPtuIds.equals(ptuIds)) {
							ptuIds = newPtuIds;
							update();
						}
					}
				});
		
		EventChangedEvent.register(eventBus, new EventChangedEvent.Handler() {
			
			@Override
			public void onEventChanged(EventChangedEvent event) {
				String eventType = event.getEvent().getEventType();
				String ptuId = event.getEvent().getDevice().getName();

				if (eventType.equals("PanicEvent")) {
					alarms.setPanic(ptuId, true);
				} else if (eventType.equals("DoseRateAlert")) {
					alarms.setDose(ptuId, true);
				} else if (eventType.equals("FallDetection")) {
					alarms.setFall(ptuId, true);
				}
				
				update();
			}
		});
	}
	
	public void clearPanicAlarm(String ptuId) {
		alarms.get(ptuId).setPanic(false);
		update();
	}

	public void clearDoseAlarm(String ptuId) {
		alarms.get(ptuId).setDose(false);
		update();
	}

	public void clearFallAlarm(String ptuId) {
		alarms.get(ptuId).setFall(false);
		update();
	}


	private void update() {		
		AlarmMapChangedRemoteEvent.fire(eventBus, alarms);
	}

	public static AlarmManager getInstance(RemoteEventBus eventBus) {
		if (instance == null) {
			instance = new AlarmManager(eventBus);
		}
		return instance;
	}


}
