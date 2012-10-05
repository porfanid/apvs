package ch.cern.atlas.apvs.server;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.service.DbService;
import ch.cern.atlas.apvs.client.service.ServiceException;
import ch.cern.atlas.apvs.client.service.SortOrder;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.client.ui.Intervention;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.view.client.Range;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class DbServiceImpl extends ResponsePollService implements DbService {

	// private static final int DEFAULT_DB_PORT = 1521;

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private String dbUrl;

	private RemoteEventBus eventBus;
	private DbHandler dbHandler;

	public DbServiceImpl() {
		log.info("Creating DbService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		log.info("Starting DbService...");

		ServerSettingsChangedEvent.subscribe(eventBus,
				new ServerSettingsChangedEvent.Handler() {

					@Override
					public void onServerSettingsChanged(
							ServerSettingsChangedEvent event) {
						ServerSettings settings = event.getServerSettings();
						if (settings != null) {
							String url = settings
									.get(ServerSettings.Entry.databaseUrl
											.toString());
							if ((url != null) && !url.equals(dbUrl)) {
								dbUrl = url;

								dbHandler.connect("jdbc:log4jdbc:oracle:thin:"
										+ dbUrl);
							}
						}
					}
				});

		dbHandler = new DbHandler(eventBus);
	}

	// store methods to follow
	@Override
	public List<Intervention> getInterventions(Range range, SortOrder[] order) throws ServiceException {
		try {
			return dbHandler.getInterventions(range, order);
		} catch (SQLException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
