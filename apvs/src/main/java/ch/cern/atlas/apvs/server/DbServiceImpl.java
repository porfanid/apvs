package ch.cern.atlas.apvs.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.service.DbService;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class DbServiceImpl extends ResponsePollService implements DbService {

//	private static final int DEFAULT_DB_PORT = 1521;

	private String dbUrl;

	private RemoteEventBus eventBus;
	private DbHandler dbHandler;

	public DbServiceImpl() {
		System.out.println("Creating DbService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		System.out.println("Starting DbService...");

		ServerSettingsChangedEvent.subscribe(eventBus,
				new ServerSettingsChangedEvent.Handler() {

					@Override
					public void onServerSettingsChanged(
							ServerSettingsChangedEvent event) {
						ServerSettings settings = event.getServerSettings();
						if (settings != null) {
							String url = settings.get(ServerSettings.Entry.databaseUrl.toString());
							if ((url != null) && !url.equals(dbUrl)) {
								dbUrl = url;
								
								dbHandler.connect("jdbc:log4jdbc:oracle:thin:"+dbUrl);
							}
						}
					}
				});

		dbHandler = new DbHandler(eventBus);
	}

	// store methods to follow
}
