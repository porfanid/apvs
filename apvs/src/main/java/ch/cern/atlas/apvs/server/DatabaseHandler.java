package ch.cern.atlas.apvs.server;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.domain.Intervention;
import ch.cern.atlas.apvs.domain.InterventionMap;
import ch.cern.atlas.apvs.domain.Ternary;
import ch.cern.atlas.apvs.event.ConnectionStatusChangedRemoteEvent;
import ch.cern.atlas.apvs.event.ConnectionStatusChangedRemoteEvent.ConnectionType;
import ch.cern.atlas.apvs.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

public class DatabaseHandler {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private static DatabaseHandler databaseHandler;

	private RemoteEventBus eventBus;
	private Database database;

	private Ternary connected = Ternary.Unknown;
	private Ternary wasConnected = connected;
	private String connectedCause = "Not Connected Yet";

	private static final long MINUTE = 60 * 1000;
	private static final long MAX_UPDATE_DELAY = 10 * MINUTE;
	private Ternary updated = Ternary.Unknown;
	private Ternary wasUpdated = updated;
	private String updatedCause = "Not Verified Yet";

	private InterventionMap interventions = new InterventionMap();

	private DatabaseHandler(final RemoteEventBus eventBus) {
		this.eventBus = eventBus;

		database = Database.getInstance();

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();

				if (type.equals(InterventionMapChangedRemoteEvent.class
						.getName())) {
					InterventionMapChangedRemoteEvent.fire(eventBus,
							interventions);
				} else if (type.equals(ConnectionStatusChangedRemoteEvent.class
						.getName())) {
					ConnectionStatusChangedRemoteEvent.fire(eventBus,
							ConnectionType.databaseConnect, connected,
							connectedCause);
					ConnectionStatusChangedRemoteEvent.fire(eventBus,
							ConnectionType.databaseUpdate, updated,
							updatedCause);
				}
			}
		});

		try {
			checkUpdateAndConnection();
			readInterventions();
		} catch (HibernateException e1) {
			log.warn("Problem", e1);
		}

		ScheduledExecutorService executor = Executors
				.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(new Runnable() {

			// ScheduledFuture<?> watchdog;

			@Override
			public void run() {
				try {
					if (!checkUpdateAndConnection()) {
						log.warn("DB " + updatedCause);
					} else if (!isConnected()) {
						log.warn("DB " + connectedCause);
					}
				} catch (HibernateException e) {
					log.warn("Could not update or reach DB: ", e);
				}

			}
		}, 0, 30, TimeUnit.SECONDS);
	}
	
	public static DatabaseHandler getInstance(RemoteEventBus eventBus) {
		if (databaseHandler == null) {
			databaseHandler = new DatabaseHandler(eventBus);
		}
		return databaseHandler;
	}	

	public boolean isConnected() {
		return connected.isTrue();
	}

	private boolean checkUpdateAndConnection() throws HibernateException {

		long now = new Date().getTime();

		try {
			Date lastUpdate = database.getLastMeasurementUpdateTime();
			if (lastUpdate != null) {
				long time = lastUpdate.getTime();
				updated = (time > now - MAX_UPDATE_DELAY) ? Ternary.True
						: Ternary.False;
				updatedCause = "Last Update: " + new Date(time);
			} else {
				updated = Ternary.False;
				updatedCause = "Never Updated";
			}
		} catch (HibernateException e) {
			connected = Ternary.False;
			connectedCause = e.getMessage();
			throw e;
		} finally {
			if (eventBus != null) {
				if (!updated.equals(wasUpdated)) {
					ConnectionStatusChangedRemoteEvent.fire(eventBus,
							ConnectionType.databaseUpdate, updated,
							updatedCause);
				}

				if (!connected.equals(wasConnected)) {
					ConnectionStatusChangedRemoteEvent.fire(eventBus,
							ConnectionType.databaseConnect, connected,
							connectedCause);
				}
			}
		}

		return !updated.isFalse();
	}

	public void readInterventions() {
		InterventionMap newMap = new InterventionMap();
		
		for (Intervention intervention : database.getInterventions() ) {
			newMap.put(intervention.getDevice(), intervention);
		}	
		
		if (!interventions.equals(newMap)) {
			interventions = newMap;
			if (eventBus != null) {
				InterventionMapChangedRemoteEvent.fire(eventBus, interventions);
			}
		}
	}

}
