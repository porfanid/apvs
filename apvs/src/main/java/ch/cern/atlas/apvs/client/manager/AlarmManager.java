package ch.cern.atlas.apvs.client.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.SerializationException;

import ch.cern.atlas.apvs.client.domain.AlarmMap;
import ch.cern.atlas.apvs.client.event.AlarmMapChangedRemoteEvent;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.EventChangedEvent;

public class AlarmManager {
	
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private static AlarmManager instance;
	private AlarmMap alarms;
	private List<Device> ptus;
	private RemoteEventBus eventBus;

	private AlarmManager(RemoteEventBus eventBus) throws SerializationException {
		this.eventBus = eventBus;
		
		alarms = new AlarmMap();
		
		// subscribe
		InterventionMapChangedRemoteEvent.subscribe(this, eventBus,
				new InterventionMapChangedRemoteEvent.Handler() {

					@Override
					public void onInterventionMapChanged(
							InterventionMapChangedRemoteEvent event) {
						// FIXME #298
						log.warn("FIXME (3x?)... Received............."
										+ event.getEventBusUUID() + " "
										+ event.getSourceUUID());
						List<Device> newPtuIds = event.getInterventionMap()
								.getPtus();
						if (!newPtuIds.equals(ptus)) {
							ptus = newPtuIds;
							update();
						}
					}
				});
		
		EventChangedEvent.register(eventBus, new EventChangedEvent.Handler() {
			
			@Override
			public void onEventChanged(EventChangedEvent event) {
				String eventType = event.getEvent().getEventType();
				Device ptu = event.getEvent().getDevice();

				if (eventType.equals("PanicEvent")) {
					alarms.setPanic(ptu, true);
				} else if (eventType.equals("DoseRateAlert")) {
					alarms.setDose(ptu, true);
				} else if (eventType.equals("FallDetection")) {
					alarms.setFall(ptu, true);
				}
				
				update();
			}
		});
	}
	
	public void clearPanicAlarm(Device ptu) {
		alarms.get(ptu).setPanic(false);
		update();
	}

	public void clearDoseAlarm(Device ptu) {
		alarms.get(ptu).setDose(false);
		update();
	}

	public void clearFallAlarm(Device ptu) {
		alarms.get(ptu).setFall(false);
		update();
	}

	private void update() {		
		try {
			AlarmMapChangedRemoteEvent.fire(eventBus, alarms);
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static AlarmManager getInstance(RemoteEventBus eventBus) throws SerializationException {
		if (instance == null) {
			instance = new AlarmManager(eventBus);
		}
		return instance;
	}


}
