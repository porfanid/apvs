package ch.cern.atlas.apvs.server;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.Intervention;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Sensor;
import ch.cern.atlas.apvs.domain.User;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.hibernate.types.DoubleStringType;
import ch.cern.atlas.apvs.hibernate.types.InetAddressType;
import ch.cern.atlas.apvs.hibernate.types.IntegerStringType;
import ch.cern.atlas.apvs.hibernate.types.MacAddressType;

public class DbCallback {
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private final boolean LOG_DB = true;

	private ServiceRegistry serviceRegistry;
	private SessionFactory sessionFactory;

	public DbCallback(RemoteEventBus eventBus) {
	}

	/**
	 * gets a session and an open transaction
	 * 
	 * @return
	 */
	protected Session getSession() {
		if (sessionFactory == null) {
			log.info("Creating SessionFactory");

			// FIXME #675 read from APVS properties
			Configuration configuration = new Configuration();
			configuration.setProperty("hibernate.connection.driver_class",
					LOG_DB ? "net.sf.log4jdbc.DriverSpy"
							: "oracle.jdbc.OracleDriver");
			configuration.setProperty("hibernate.connection.url", "jdbc:"
					+ (LOG_DB ? "log4jdbc:" : "")
					+ "oracle:thin:@//pcatlaswpss01:1521/XE");
			configuration.setProperty("hibernate.connection.username",
					"wpss_public");
			configuration
					.setProperty("hibernate.connection.password", "public");

			configuration.setProperty("show_sql", "true");

			configuration.setProperty("hibernate.c3p0.min_size", "5");
			configuration.setProperty("hibernate.c3p0.max_size", "20");
			configuration.setProperty("hibernate.c3p0.timeout", "1800");
			configuration.setProperty("hibernate.c3p0.max_statements", "50");

			configuration.addAnnotatedClass(Device.class);
			configuration.addAnnotatedClass(Event.class);
			configuration.addAnnotatedClass(Intervention.class);
			configuration.addAnnotatedClass(Measurement.class);
			configuration.addAnnotatedClass(Sensor.class);
			configuration.addAnnotatedClass(User.class);

			configuration.registerTypeOverride(new DoubleStringType());
			configuration.registerTypeOverride(new IntegerStringType());
			configuration.registerTypeOverride(new MacAddressType());
			configuration.registerTypeOverride(new InetAddressType());

			serviceRegistry = new ServiceRegistryBuilder().applySettings(
					configuration.getProperties()).buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}

		Session session = sessionFactory.openSession();
		log.info("Open Session "+session.hashCode());
		
		return session;
	}

	public void dbConnected(Session session) {
	}

	public void dbDisconnected() {
	}

	public boolean isConnected() throws HibernateException {
		Session session = null;
		Transaction tx = null;
		try {
			session = getSession();
			tx = session.beginTransaction();		
			boolean connected = session.isConnected();
			tx.commit();
			return connected;
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public boolean checkConnection() {
		return isConnected();
	}
	/*
	 * public void connect(final String url) { disconnect();
	 * 
	 * connectFuture = executorService.submit(new Runnable() {
	 * 
	 * @Override public void run() { try { datasource = new
	 * ComboPooledDataSource();
	 * 
	 * datasource .setDriverClass(LOG_DB ? "net.sf.log4jdbc.DriverSpy" :
	 * "oracle.jdbc.OracleDriver"); int pos = url.indexOf("@//"); if (pos <= 0)
	 * { log.warn("Username 'user@//' not found in " + url); return; }
	 * 
	 * String pwd = ServerSettingsStorage.getInstance(eventBus) .getPasswords()
	 * .get(ServerSettings.Entry.databaseUrl.toString()); if ((pwd == null) ||
	 * pwd.equals("")) { log.warn("DB pwd 'null' or empty"); return; }
	 * 
	 * String shortUrl = url.substring(pos); String user = url.substring(0,
	 * pos); System.err.println("Loging in to " + user + " " + shortUrl + " " +
	 * pwd);
	 * 
	 * datasource.setJdbcUrl("jdbc:" + (LOG_DB ? "log4jdbc:" : "") +
	 * "oracle:thin:" + shortUrl); datasource.setUser(user);
	 * datasource.setPassword(pwd);
	 * 
	 * // FIXME check if this helps...
	 * datasource.setMaxStatementsPerConnection(30);
	 * 
	 * dbConnected(datasource);
	 * 
	 * } catch (SQLException e) { exceptionCaught(e); } catch
	 * (PropertyVetoException e) { exceptionCaught(e); } } });
	 * 
	 * }
	 * 
	 * public void disconnect() { if (connectFuture != null) {
	 * connectFuture.cancel(true); }
	 * 
	 * if (datasource == null) { return; }
	 * 
	 * if (disconnectFuture != null) { return; }
	 * 
	 * disconnectFuture = executorService.submit(new Runnable() {
	 * 
	 * @Override public void run() { try { datasource.close(); datasource =
	 * null; dbDisconnected(); } catch (SQLException e) { exceptionCaught(e); }
	 * } }); }
	 * 
	 * // REMOVE hibernate protected Connection getConnection() throws
	 * SQLException { if (datasource == null || datasource.getPassword() ==
	 * null) { throw new SQLException("No Datasource (yet)"); } return
	 * datasource.getConnection(); }
	 */
}
