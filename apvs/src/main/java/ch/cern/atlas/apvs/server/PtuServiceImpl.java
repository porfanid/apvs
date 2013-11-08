package ch.cern.atlas.apvs.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.manager.AlarmManager;
import ch.cern.atlas.apvs.client.service.PtuService;
import ch.cern.atlas.apvs.client.service.ServiceException;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.MeasurementConfiguration;
import ch.cern.atlas.apvs.domain.Order;
import ch.cern.atlas.apvs.domain.SortOrder;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.server.PtuChannelInitializer;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class PtuServiceImpl extends ResponsePollService implements PtuService {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private static final int DEFAULT_PTU_PORT = 4005;

	private String ptuUrl;

	private RemoteEventBus eventBus;
	private PtuClientHandler ptuClientHandler;

	private Database database;

	public PtuServiceImpl() throws SerializationException {
		log.info("Creating PtuService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();

		PtuSettingsStorage.getInstance(eventBus);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		log.info("Starting PtuService...");

		database = Database.getInstance();

		Map<String, Device> devices = database.getDeviceMap();

		EventLoopGroup group = new NioEventLoopGroup();

		// FIXME - Change the declaring location before
		// ServerSettingsChangedRemoteEvent
		// Configure the client.
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);

		// FIXME - Change the declaring location before
		// ServerSettingsChangedRemoteEvent
		try {
			ptuClientHandler = new PtuClientHandler(bootstrap, eventBus);
		} catch (SerializationException e) {
			throw new ServletException(e);
		}

		// Configure the pipeline factory.
		bootstrap.group(group);
		bootstrap.handler(new PtuChannelInitializer(ptuClientHandler, devices, true));

		ServerSettingsChangedRemoteEvent.subscribe(eventBus,
				new ServerSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onServerSettingsChanged(
							ServerSettingsChangedRemoteEvent event) {
						ServerSettings settings = event.getServerSettings();
						if (settings != null) {
							String url = settings
									.get(ServerSettings.Entry.ptuUrl.toString());
							if ((url != null) && !url.equals(ptuUrl)) {
								ptuUrl = url;
								String[] s = ptuUrl.split(":", 2);
								String host = s[0];
								int port = s.length > 1 ? Integer
										.parseInt(s[1]) : DEFAULT_PTU_PORT;

								log.info("Setting PTU to " + host + ":" + port);
								ptuClientHandler.connect(new InetSocketAddress(
										host, port));
							}
						}
					}
				});

	}

	@Override
	public long getRowCount() throws ServiceException {
//		return database.getCount(MeasurementConfiguration.class);
		return database.getMeasurementConfigurationList().size();
	}

	@Override
	public List<MeasurementConfiguration> getTableData(Integer start,
			Integer length, List<SortOrder> sortOrder) throws ServiceException {
//		return database.getList(MeasurementConfiguration.class, start, length,
//				Database.getOrder(sortOrder), null, Arrays.asList("device"));
		return database.getMeasurementConfigurationList();
	}

	@Override
	public List<Measurement> getMeasurements(List<Device> ptuList, String name)
			throws ServiceException {
		try {
			return database.getMeasurements(ptuList, name);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Measurement> getMeasurements(Device ptu, String name)
			throws ServiceException {
		try {
			return database.getMeasurements(ptu, name);
		} catch (HibernateException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public History getHistory(List<Device> devices, Date from,
			Integer maxEntries) throws ServiceException {
		try {
			System.err.println(devices.size() + " " + from + " " + maxEntries);
			return database.getHistory(devices, from, maxEntries);
		} catch (HibernateException e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void handleOrder(Order order) throws ServiceException {
		System.err.println("Handle " + order);

		ptuClientHandler.sendOrder(order);
	}

	@Override
	public void clearPanicAlarm(Device ptu) throws ServiceException {
		System.err.println("Clearing panic for " + ptu);
		try {
			AlarmManager.getInstance(eventBus).clearPanicAlarm(ptu);
		} catch (SerializationException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void clearDoseAlarm(Device ptu) throws ServiceException {
		try {
			AlarmManager.getInstance(eventBus).clearDoseAlarm(ptu);
		} catch (SerializationException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void clearFallAlarm(Device ptu) throws ServiceException {
		try {
			AlarmManager.getInstance(eventBus).clearFallAlarm(ptu);
		} catch (SerializationException e) {
			throw new ServiceException(e);
		}
	}
}
