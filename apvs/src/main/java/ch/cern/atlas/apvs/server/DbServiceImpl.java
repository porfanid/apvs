package ch.cern.atlas.apvs.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.service.DbService;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class DbServiceImpl extends ResponsePollService implements DbService {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private String dbUrl;

	private RemoteEventBus eventBus;
	private Database database;
	private DatabaseHandler databaseHandler;

	public DbServiceImpl() {
		log.info("Creating DbService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		log.info("Starting DbService...");
		database = Database.getInstance();
		databaseHandler = DatabaseHandler.getInstance(eventBus);
		
		ServerSettingsChangedRemoteEvent.subscribe(eventBus,
				new ServerSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onServerSettingsChanged(
							ServerSettingsChangedRemoteEvent event) {
						ServerSettings settings = event.getServerSettings();
						if (settings != null) {
							String url = settings
									.get(ServerSettings.Entry.databaseUrl
											.toString());
							if ((url != null) && !url.equals(dbUrl)) {
								dbUrl = url;
								
								// Check DB
								
								// FIXME move and check others
								try {
									database.getDevices(false);
									database.getDevices(true);
									database.getUsers(false);
									database.getUsers(true);
									database.getSensorMap();
								} catch (HibernateException e) {
									e.printStackTrace();
									System.exit(1);
								}

							}
						}
					}
				});
	}
	
	@Override
	public boolean isConnected() {
		return databaseHandler.isConnected();
	}	
}
