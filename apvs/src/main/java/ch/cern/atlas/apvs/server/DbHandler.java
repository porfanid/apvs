package ch.cern.atlas.apvs.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.domain.Device;
import ch.cern.atlas.apvs.client.domain.Intervention;
import ch.cern.atlas.apvs.client.domain.User;
import ch.cern.atlas.apvs.client.event.ConnectionStatusChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.ConnectionStatusChangedRemoteEvent.ConnectionType;
import ch.cern.atlas.apvs.client.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.client.service.SortOrder;
import ch.cern.atlas.apvs.client.settings.InterventionMap;
import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;

import com.google.gwt.view.client.Range;

public class DbHandler extends DbReconnectHandler {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	private final RemoteEventBus eventBus;

	private InterventionMap interventions = new InterventionMap();

	public DbHandler(final RemoteEventBus eventBus) {
		super();
		this.eventBus = eventBus;

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();

				if (type.equals(InterventionMapChangedRemoteEvent.class.getName())) {
					InterventionMapChangedRemoteEvent.fire(eventBus, interventions);
				} else if (type.equals(ConnectionStatusChangedRemoteEvent.class
						.getName())) {
					ConnectionStatusChangedRemoteEvent.fire(eventBus,
							ConnectionType.database, isConnected());
				}
			}
		});

		MeasurementChangedEvent.register(eventBus,
				new MeasurementChangedEvent.Handler() {

					@Override
					public void onMeasurementChanged(
							MeasurementChangedEvent event) {
						Measurement m = event.getMeasurement();
						String key = m.getName();
						String oldUnit = units.get(key);
						if (oldUnit == null || !oldUnit.equals(m.getUnit())) {
							units.put(key, m.getUnit());
						}
					}
				});

		ScheduledExecutorService executor = Executors
				.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (isConnected()) {
					if (!checkConnection()) {
						log.warn("DB no longer reachable, closing");
						disconnect();
					}
					try {
						updateInterventions();
					} catch (SQLException e) {
						log.warn(
								"Could not regularly-update intervention list: ",
								e);
					}
				}
			}
		}, 0, 30, TimeUnit.SECONDS);
	}

	public List<History> getHistories(String ptuId, String sensor)
			throws SQLException {
		String sql = "select NAME, SENSOR, DATETIME, UNIT, VALUE from tbl_measurements, tbl_devices "
				+ "where tbl_measurements.device_id = tbl_devices.id";

		if (sensor != null) {
			sql += " and SENSOR = ?";
		}
		if (ptuId != null) {
			sql += " and NAME = ?";
		}

		sql += " order by DATETIME desc";

		Connection connection = getConnection();
		PreparedStatement historyQuery = connection.prepareStatement(sql);

		int param = 1;
		if (sensor != null) {
			historyQuery.setString(param, sensor);
			param++;
		}
		if (ptuId != null) {
			historyQuery.setString(param, ptuId);
			param++;
		}

		long PERIOD = 36; // hours
		int MAX_ENTRIES = 1000; // number
		long MIN_INTERVAL = 5000; // ms

		Map<String, String> ptuIds = new HashMap<String, String>();
		Map<String, String> sensors = new HashMap<String, String>();
		Map<String, Deque<Number[]>> data = new HashMap<String, Deque<Number[]>>();
		Map<String, String> units = new HashMap<String, String>();
		Map<String, Long> lastTimes = new HashMap<String, Long>();

		long time = 0;
		long then = -1;
		ResultSet result = historyQuery.executeQuery();
		try {
			while (result.next() && (data.size() < MAX_ENTRIES)) {
				time = result.getTimestamp("datetime").getTime();
				if (then < 0) {
					then = time - (PERIOD * 60 * 60 * 1000);
				}
				if (time < then) {
					break;
				}

				ptuId = result.getString("name");
				sensor = result.getString("sensor");
				String key = ptuId + ":" + sensor;
				ptuIds.put(key, ptuId);
				sensors.put(key, sensor);
				lastTimes.put(key, new Date().getTime() + MIN_INTERVAL);

				String unit = result.getString("unit");
				double value = Double.parseDouble(result.getString("value"));

				// Scale down to microSievert
				if (unit.equals("mSv")) {
					unit = "&micro;Sv";
					value *= 1000;
				}
				if (unit.equals("mSv/h")) {
					unit = "&micro;Sv/h";
					value *= 1000;
				}
				units.put(key, unit);

				// limit entry separation (reverse order)
				if (lastTimes.get(key) - time > MIN_INTERVAL) {
					lastTimes.put(key, time);

					Number[] entry = new Number[2];
					entry[0] = time;
					entry[1] = value;
					Deque<Number[]> deque = data.get(key);
					if (deque == null) {
						deque = new ArrayDeque<Number[]>(200);
						data.put(key, deque);
					}
					deque.addFirst(entry);
				}
			}
		} finally {
			result.close();
			historyQuery.close();
			connection.close();
		}

		List<History> list = new ArrayList<History>(data.size());
		for (Entry<String, Deque<Number[]>> e : data.entrySet()) {
			String key = e.getKey();
			ptuId = ptuIds.get(key);
			sensor = sensors.get(key);
			list.add(new History(ptuId, sensor, e.getValue().toArray(
					new Number[data.size()][]), units.get(key)));
			log.info("Creating history for " + ptuId + " " + sensor + " "
					+ data.size() + " entries");
		}
		return list;
	}

	@Override
	public void dbConnected(DataSource datasource) throws SQLException {
		super.dbConnected(datasource);

		ConnectionStatusChangedRemoteEvent.fire(eventBus, ConnectionType.database,
				true);

		updateInterventions();
	}

	@Override
	public void dbDisconnected() throws SQLException {
		super.dbDisconnected();

		ConnectionStatusChangedRemoteEvent.fire(eventBus, ConnectionType.database,
				false);

		interventions.clear();
		InterventionMapChangedRemoteEvent.fire(eventBus, interventions);
	}

	private String getSql(String sql, Range range, SortOrder[] order) {
		StringBuffer s = new StringBuffer(sql);
		for (int i = 0; i < order.length; i++) {
			if (i == 0) {
				s.append(" order by ");
			}
			s.append(order[i].getName());
			s.append(" ");
			s.append(order[i].isAscending() ? "ASC" : "DESC");
			if (i + 1 < order.length) {
				s.append(", ");
			}
		}
		return s.toString();
	}

	private int getCount(PreparedStatement statement) throws SQLException {
		ResultSet result = statement.executeQuery();

		try {
			return result.next() ? result.getInt(1) : 0;
		} finally {
			result.close();
			statement.close();
		}
	}

	public int getInterventionCount() throws SQLException {
		Connection connection = getConnection();

		try {
			return getCount(connection.prepareStatement(
					"select count(*) from tbl_inspections"));
		} finally {
			connection.close();
		}
	}

	public List<Intervention> getInterventions(Range range, SortOrder[] order)
			throws SQLException {
		String sql = "select tbl_inspections.id, tbl_users.fname, tbl_users.lname, tbl_devices.name, "
				+ "tbl_inspections.starttime, tbl_inspections.endtime, tbl_inspections.dscr, tbl_users.id, tbl_devices.id "
				+ "from tbl_inspections "
				+ "join tbl_users on tbl_inspections.user_id = tbl_users.id "
				+ "join tbl_devices on tbl_inspections.device_id = tbl_devices.id";

		Connection connection = getConnection();
		PreparedStatement statement = connection.prepareStatement(
				getSql(sql, range, order));
		ResultSet result = statement.executeQuery();

		List<Intervention> list = new ArrayList<Intervention>(range.getLength());
		try {
			// FIXME, #173 using some SQL this may be faster
			// skip to start, result.absolute not implemented by Oracle
			for (int i = 0; i < range.getStart() && result.next(); i++) {
			}

			for (int i = 0; i < range.getLength() && result.next(); i++) {
				list.add(new Intervention(result.getInt(1), result.getInt(8),
						result.getString(2), result.getString(3), result
								.getInt(9), result.getString(4), new Date(
								result.getTimestamp(5).getTime()), result
								.getTimestamp(6) != null ? new Date(result
								.getTimestamp(6).getTime()) : null, result
								.getString(7)));
			}
		} finally {
			result.close();
			statement.close();
			connection.close();
		}

		return list;
	}

	public int getEventCount(String ptuId, String measurementName)
			throws SQLException {
		String sql = "select count(*) from tbl_events join tbl_devices on tbl_events.device_id = tbl_devices.id";
		if ((ptuId != null) || (measurementName != null)) {
			sql += " where";
			if (ptuId != null) {
				sql += " tbl_devices.name = ?";
			}
			if (measurementName != null) {
				if (ptuId != null) {
					sql += " and";
				}
				sql += " tbl_events.sensor = ?";
			}
		}

		Connection connection = getConnection();

		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			int param = 1;
			if (ptuId != null) {
				statement.setString(param, ptuId);
				param++;
			}
			if (measurementName != null) {
				statement.setString(param, measurementName);
				param++;
			}

			return getCount(statement);
		} finally {
			connection.close();
		}
	}

	// FIXME #231 until unit is in the DB
	private Map<String, String> units = new HashMap<String, String>();

	public List<Event> getEvents(Range range, SortOrder[] order, String ptuId,
			String measurementName) throws SQLException {

		String sql = "select tbl_devices.name, tbl_events.sensor, tbl_events.event_type, "
				+ "tbl_events.value, tbl_events.threshold, tbl_events.datetime "
				+ "from tbl_events "
				+ "join tbl_devices on tbl_events.device_id = tbl_devices.id";
		if ((ptuId != null) || (measurementName != null)) {
			sql += " where";
			if (ptuId != null) {
				sql += " tbl_devices.name = ?";
			}
			if (measurementName != null) {
				if (ptuId != null) {
					sql += " and";
				}
				sql += " tbl_events.sensor = ?";
			}
		}

		Connection connection = getConnection();
		PreparedStatement statement = connection.prepareStatement(
				getSql(sql, range, order));
		int param = 1;
		if (ptuId != null) {
			statement.setString(param, ptuId);
			param++;
		}
		if (measurementName != null) {
			statement.setString(param, measurementName);
			param++;
		}

		ResultSet result = statement.executeQuery();

		List<Event> list = new ArrayList<Event>(range.getLength());
		try {
			// FIXME, #173 using some SQL this may be faster
			// skip to start, result.absolute not implemented by Oracle
			for (int i = 0; i < range.getStart() && result.next(); i++) {
			}

			for (int i = 0; i < range.getLength() && result.next(); i++) {
				String name = result.getString("name");
				double value = getDouble(result.getString("value"));
				double threshold = getDouble(result.getString("threshold"));
				String unit = units.get(name) != null ? units.get(name) : "";

				list.add(new Event(name, result.getString("sensor"), result
						.getString("event_type"), value, threshold, unit,
						new Date(result.getTimestamp("datetime").getTime())));
			}
		} finally {
			result.close();
			statement.close();
			connection.close();
		}
		return list;
	}

	private double getDouble(String string) {
		if (string == null)
			return 0;

		return Double.parseDouble(string);
	}

	public synchronized void addUser(User user) throws SQLException {
		Connection connection = getConnection();
		PreparedStatement addUser = connection
				.prepareStatement(
						"insert into tbl_users (fname, lname, cern_id) values (?, ?, ?)");
		try {
			addUser.setString(1, user.getFirstName());
			addUser.setString(2, user.getLastName());
			addUser.setString(3, user.getCernId());
			addUser.executeUpdate();
		} finally {
			addUser.close();
			connection.close();
		}
	}

	public synchronized void addDevice(Device device) throws SQLException {
		Connection connection = getConnection();
		PreparedStatement addDevice = connection
				.prepareStatement("insert into tbl_devices (name, ip, dscr) values (?, ?, ?)");
		try {
			addDevice.setString(1, device.getName());
			addDevice.setString(2, device.getIp());
			addDevice.setString(3, device.getDescription());
			addDevice.executeUpdate();
		} finally {
			addDevice.close();
			connection.close();
		}
	}

	public synchronized void addIntervention(Intervention intervention)
			throws SQLException {
		Connection connection = getConnection();
		PreparedStatement addIntervention = connection
				.prepareStatement(
						"insert into tbl_inspections (user_id, device_id, starttime, dscr) values (?, ?, ?, ?)");

		try {
			addIntervention.setInt(1, intervention.getUserId());
			addIntervention.setInt(2, intervention.getDeviceId());
			addIntervention.setTimestamp(3, new Timestamp(intervention
					.getStartTime().getTime()));
			addIntervention.setString(4, intervention.getDescription());
			addIntervention.executeUpdate();

			updateInterventions();
		} finally {
			addIntervention.close();
			connection.close();
		}
	}

	public synchronized void endIntervention(int id, Date date)
			throws SQLException {
		Connection connection = getConnection();
		PreparedStatement endIntervention = connection.prepareStatement(
				"update tbl_inspections set endtime = ? where id = ?");

		try {
			endIntervention.setTimestamp(1, new Timestamp(date.getTime()));
			endIntervention.setInt(2, id);
			endIntervention.executeUpdate();

			updateInterventions();
		} finally {
			endIntervention.close();
			connection.close();
		}
	}

	public List<User> getUsers(boolean notBusy) throws SQLException {
		return notBusy ? getNotBusyUsers() : getAllUsers();
	}

	private synchronized List<User> getNotBusyUsers() throws SQLException {
		Connection connection = getConnection();
		PreparedStatement notBusyUserQuery = connection.prepareStatement(
				"select ID, FNAME, LNAME, CERN_ID from tbl_users "
						+ "where id not in ("
						+ "select user_id from tbl_inspections "
						+ "where endtime is null) " + "order by LNAME, FNAME");
		try {
			return getUserList(notBusyUserQuery.executeQuery());
		} finally {
			notBusyUserQuery.close();
			connection.close();
		}
	}

	private synchronized List<User> getAllUsers() throws SQLException {
		Connection connection = getConnection();
		PreparedStatement userQuery = connection
				.prepareStatement(
						"select ID, FNAME, LNAME, CERN_ID from tbl_users order by LNAME, FNAME");

		try {
			return getUserList(userQuery.executeQuery());
		} finally {
			userQuery.close();
			connection.close();
		}
	}

	private synchronized List<User> getUserList(ResultSet result)
			throws SQLException {
		List<User> list = new ArrayList<User>();
		try {
			while (result.next()) {
				list.add(new User(result.getInt("ID"), result
						.getString("FNAME"), result.getString("LNAME"), result
						.getString("CERN_ID")));
			}
		} finally {
			result.close();
		}
		return list;
	}

	public List<Device> getDevices(boolean notBusy) throws SQLException {
		return notBusy ? getNotBusyDevices() : getDevices();
	}

	private synchronized List<Device> getNotBusyDevices() throws SQLException {
		Connection connection = getConnection();
		PreparedStatement notBusyDeviceQuery = connection
				.prepareStatement(
						"select ID, NAME, IP, DSCR from tbl_devices "
								+ "where id not in ("
								+ "select device_id from tbl_inspections "
								+ "where endtime is null) " + "order by NAME");

		try {
			return getDeviceList(notBusyDeviceQuery.executeQuery());
		} finally {
			notBusyDeviceQuery.close();
			connection.close();
		}
	}

	private List<Device> getDevices() throws SQLException {
		Connection connection = getConnection();
		PreparedStatement deviceQuery = connection.prepareStatement(
				"select ID, NAME, IP, DSCR from tbl_devices order by NAME");

		try {
			return getDeviceList(deviceQuery.executeQuery());
		} finally {
			deviceQuery.close();
			connection.close();
		}
	}

	private synchronized List<Device> getDeviceList(ResultSet result)
			throws SQLException {
		List<Device> list = new ArrayList<Device>();
		try {
			while (result.next()) {
				list.add(new Device(result.getInt("ID"), result
						.getString("NAME"), result.getString("IP"), result
						.getString("DSCR")));
			}
		} finally {
			result.close();
		}
		return list;
	}

	public synchronized void updateInterventionDescription(int id,
			String description) throws SQLException {
		Connection connection = getConnection();
		PreparedStatement updateInterventionDescription = connection
				.prepareStatement(
						"update tbl_inspections set dscr = ? where id=?");

		try {
			updateInterventionDescription.setString(1, description);
			updateInterventionDescription.setInt(2, id);
			updateInterventionDescription.executeUpdate();

			updateInterventions();
		} finally {
			updateInterventionDescription.close();
			connection.close();
		}
	}

	public synchronized Intervention getIntervention(String ptuId)
			throws SQLException {
		Connection connection = getConnection();
		PreparedStatement getIntervention = connection
				.prepareStatement(
						"select tbl_inspections.ID, tbl_inspections.USER_ID, tbl_users.FNAME, tbl_users.LNAME, tbl_inspections.DEVICE_ID, tbl_devices.NAME, tbl_inspections.STARTTIME, tbl_inspections.ENDTIME, tbl_inspections.DSCR from tbl_inspections "
								+ "join tbl_devices on tbl_inspections.device_id = tbl_devices.id "
								+ "join tbl_users on tbl_inspections.user_id = tbl_users.id "
								+ "where tbl_inspections.endtime is null "
								+ "and tbl_devices.name = ? "
								+ "order by starttime desc");

		getIntervention.setString(1, ptuId);
		ResultSet result = getIntervention.executeQuery();
		try {
			if (result.next()) {
				return new Intervention(result.getInt("id"),
						result.getInt("user_id"), result.getString("fname"),
						result.getString("lname"), result.getInt("device_id"),
						result.getString("name"),
						result.getTimestamp("starttime"),
						result.getTimestamp("endtime"),
						result.getString("dscr"));
			}
		} finally {
			result.close();
			getIntervention.close();
			connection.close();
		}
		return null;
	}

	private synchronized void updateInterventions() throws SQLException {
		Connection connection = getConnection();
		PreparedStatement updateInterventions = connection
				.prepareStatement(
						"select tbl_inspections.ID, tbl_inspections.USER_ID, tbl_users.FNAME, tbl_users.LNAME, tbl_inspections.DEVICE_ID, tbl_devices.NAME, tbl_inspections.STARTTIME, tbl_inspections.ENDTIME, tbl_inspections.DSCR from tbl_inspections "
								+ "join tbl_devices on tbl_inspections.device_id = tbl_devices.id "
								+ "join tbl_users on tbl_inspections.user_id = tbl_users.id "
								+ "where tbl_inspections.endtime is null");

		ResultSet result = updateInterventions.executeQuery();
		InterventionMap newMap = new InterventionMap();
		try {
			while (result.next()) {
				String name = result.getString("name");
				newMap.put(
						name,
						new Intervention(result.getInt("id"), result
								.getInt("user_id"), result.getString("fname"),
								result.getString("lname"), result
										.getInt("device_id"), name, result
										.getTimestamp("starttime"), result
										.getTimestamp("endtime"), result
										.getString("dscr")));
			}
		} finally {
			result.close();
			updateInterventions.close();
			connection.close();
		}

		if (!interventions.equals(newMap)) {
			interventions = newMap;
			InterventionMapChangedRemoteEvent.fire(eventBus, interventions);
		}
	}

	/**
	 * 
	 * @param ptuId
	 *            can be null
	 * @param name
	 *            can be null
	 * @return
	 * @throws SQLException
	 */
	public List<Measurement> getMeasurements(String ptuId, String name)
			throws SQLException {

		String sql = "select NAME, view_last_measurements_date.SENSOR, VALUE, SAMPLINGRATE, UNIT, view_last_measurements_date.DATETIME "
				+ "from view_last_measurements_date, tbl_measurements, tbl_devices "
				+ "where view_last_measurements_date.datetime = tbl_measurements.datetime "
				+ "and view_last_measurements_date.sensor = tbl_measurements.sensor "
				+ "and view_last_measurements_date.device_id = tbl_measurements.device_id "
				+ "and view_last_measurements_date.device_id = tbl_devices.id";
		if (ptuId != null) {
			sql += " and NAME = ?";
		}
		if (name != null) {
			sql += " and view_last_measurements_date.SENSOR = ?";
		}

		Connection connection = getConnection();
		PreparedStatement statement = connection.prepareStatement(sql);
		int param = 1;
		if (ptuId != null) {
			statement.setString(param, ptuId);
			param++;
		}
		if (name != null) {
			statement.setString(param, name);
			param++;
		}

		ResultSet result = statement.executeQuery();

		List<Measurement> list = new ArrayList<Measurement>();
		try {
			while (result.next()) {
				String unit = result.getString("UNIT");
				double value = Double.parseDouble(result.getString("VALUE"));

				// Scale down to microSievert
				if (unit.equals("mSv")) {
					unit = "&micro;Sv";
					value *= 1000;
				}
				if (unit.equals("mSv/h")) {
					unit = "&micro;Sv/h";
					value *= 1000;
				}
				Measurement m = new Measurement(result.getString("NAME"),
						result.getString("SENSOR"), value,
						Integer.parseInt(result.getString("SAMPLINGRATE")),
						unit, new Date(result.getTimestamp("DATETIME")
								.getTime()));
				list.add(m);
			}
		} finally {
			result.close();
			statement.close();
			connection.close();
		}
		return list;
	}
}
