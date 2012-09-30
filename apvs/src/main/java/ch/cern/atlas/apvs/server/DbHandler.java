package ch.cern.atlas.apvs.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.server.PtuServerConstants;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

public class DbHandler extends DbReconnectHandler {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	private final RemoteEventBus eventBus;

	private Ptus ptus = Ptus.getInstance();

	private PreparedStatement historyQueryCount;
	private PreparedStatement historyQuery;
	private PreparedStatement deviceQuery;
	private PreparedStatement userQuery;

	public DbHandler(final RemoteEventBus eventBus) {
		super();
		this.eventBus = eventBus;

		ptus.setDbHandler(this);

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();

				if (type.equals(PtuIdsChangedEvent.class.getName())) {
					eventBus.fireEvent(new PtuIdsChangedEvent(ptus.getPtuIds()));
				}
			}
		});

		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
				1);
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					updateDevices();
				} catch (SQLException e) {
					log.warn("Could not regularly-update device list: ", e);
				}
				try {
					updateUsers();
				} catch (SQLException e) {
					log.warn("Could not regularly-update user list: ", e);
				}
			}
		}, 30, 30, TimeUnit.SECONDS);
	}

	public History getHistory(String ptuId, String sensor, String unit) {
		History history = null;
		// check on history and load from DB
		if ((historyQuery != null) && (historyQueryCount != null)) {

			long PERIOD = 36; // hours
			Date then = new Date(new Date().getTime() - (PERIOD * 3600000));
			String timestamp = PtuServerConstants.timestampFormat.format(then);

			try {
				historyQueryCount.setString(1, sensor);
				historyQueryCount.setString(2, ptuId);
				historyQueryCount.setString(3, timestamp);
				historyQuery.setString(1, sensor);
				historyQuery.setString(2, ptuId);
				historyQuery.setString(3, timestamp);

				ResultSet result = historyQueryCount.executeQuery();
				result.next();

				int n = result.getInt(1);
				result.close();

				int MAX_ENTRIES = 1000;
				long MIN_INTERVAL = 5000; // ms

				if (n > 0) {
					// limit entries
					if (n > MAX_ENTRIES)
						n = 1000;

					Deque<Number[]> data = new ArrayDeque<Number[]>(n);

					long lastTime = new Date().getTime();
					result = historyQuery.executeQuery();
					while (result.next() && (data.size() <= n)) {
						long time = result.getTimestamp(1).getTime();

						// limit entry separation (reverse order)
						if (lastTime - time > MIN_INTERVAL) {
							lastTime = time;

							Number[] entry = new Number[2];
							entry[0] = time;
							entry[1] = Double.parseDouble(result.getString(2));
							data.addFirst(entry);
						}
					}
					result.close();

					log.info("Creating history for " + ptuId + " " + sensor
							+ " " + data.size() + " entries");
					history = new History(
							data.toArray(new Number[data.size()][]), unit);

				}
			} catch (SQLException ex) {
				log.warn("Exception", ex);
			}
		}
		return history;
	}

	@Override
	public void dbConnected(Connection connection) throws SQLException {
		super.dbConnected(connection);

		connection.setAutoCommit(true);

		historyQueryCount = connection
				.prepareStatement("select count(*) from tbl_measurements "
						+ "join tbl_devices on tbl_measurements.device_id = tbl_devices.id "
						+ "where sensor = ? " + "and name = ? "
						+ "and datetime > to_timestamp(?,"
						+ PtuServerConstants.oracleFormat + ")");

		historyQuery = connection
				.prepareStatement("select DATETIME, VALUE from tbl_measurements "
						+ "join tbl_devices on tbl_measurements.device_id = tbl_devices.id "
						+ "where SENSOR = ? "
						+ "and NAME = ? "
						+ "and DATETIME > to_timestamp(?,"
						+ PtuServerConstants.oracleFormat
						+ ") "
						+ "order by DATETIME desc");

		deviceQuery = connection
				.prepareStatement("select ID, NAME from tbl_devices");

		userQuery = connection
				.prepareStatement("select ID, FNAME, LNAME from tbl_users");

		updateDevices();
		updateUsers();
	}

	private void updateDevices() throws SQLException {
		if (deviceQuery == null)
			return;

		Set<String> prune = new HashSet<String>();
		prune.addAll(ptus.getPtuIds());

		boolean ptuIdsChanged = false;
		ResultSet result = deviceQuery.executeQuery();
		while (result.next()) {
			// int id = result.getInt(1);
			String ptuId = result.getString(2);

			Ptu ptu = ptus.get(ptuId);
			if (ptu == null) {
				ptu = new Ptu(ptuId);
				ptus.put(ptuId, ptu);
				ptuIdsChanged = true;
			} else {
				prune.remove(ptuId);
			}
		}

		log.info("Pruning " + prune.size() + " devices...");
		for (Iterator<String> i = prune.iterator(); i.hasNext();) {
			ptus.remove(i.next());
			ptuIdsChanged = true;
		}

		if (ptuIdsChanged) {
			eventBus.fireEvent(new PtuIdsChangedEvent(ptus.getPtuIds()));
			ptuIdsChanged = false;
		}
	}

	private void updateUsers() throws SQLException {
		if (userQuery == null)
			return;

		// FIXME...
	}
}
