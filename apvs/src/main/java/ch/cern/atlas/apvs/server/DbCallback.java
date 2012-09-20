package ch.cern.atlas.apvs.server;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbCallback {
	
	private Driver driver = new net.sf.log4jdbc.DriverSpy();

	private Connection connection;
	private boolean driverRegistered = false;

	public void dbConnected(Connection connection) throws SQLException {
	}

	public void dbDisconnected() throws SQLException {
	}

	public void exceptionCaught(Exception e) {
	}

	public boolean isConnected() {
		return connection != null;
	}

	public void connect(String url) {
		try {
			if (!driverRegistered) {
				// DriverManager.registerDriver(new
				// oracle.jdbc.driver.OracleDriver());
				DriverManager.registerDriver(driver);
				driverRegistered = true;
			}
			connection = DriverManager.getConnection(url);
			dbConnected(connection);
		} catch (SQLException e) {
			exceptionCaught(e);
		}
	}

	public void disconnect() {
		if (connection == null)
			return;

		try {
			connection.close();
			connection = null;
			dbDisconnected();
		} catch (SQLException e) {
			exceptionCaught(e);
		}
	}
}
