package ch.cern.atlas.apvs.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.dosimeter.server.DosimeterClientHandler;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.server.PtuPipelineFactory;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class DosimeterServiceImpl extends ResponsePollService {

	private final Logger log = Logger.getLogger(getClass().getName());
	private static final int DEFAULT_PORT = 4001;

	private String dosimeterUrl;

	private DosimeterClientHandler dosimeterClientHandler;
	private RemoteEventBus eventBus;

	private boolean ENABLE_DOSIMETER = false;

	public DosimeterServiceImpl() {
		if (ENABLE_DOSIMETER) {
			log.info("Creating DosimeterService...");
			eventBus = APVSServerFactory.getInstance().getEventBus();
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		if (ENABLE_DOSIMETER) {

			log.info("Starting DosimeterService...");

			ServerSettingsChangedRemoteEvent.subscribe(eventBus,
					new ServerSettingsChangedRemoteEvent.Handler() {

						@Override
						public void onServerSettingsChanged(
								ServerSettingsChangedRemoteEvent event) {
							ServerSettings settings = event.getServerSettings();
							if (settings != null) {
								String url = settings
										.get(ServerSettings.Entry.dosimeterUrl
												.toString());
								if ((url != null) && !url.equals(dosimeterUrl)) {
									dosimeterUrl = url;
									String[] s = dosimeterUrl.split(":", 2);
									String host = s[0];
									int port = s.length > 1 ? Integer
											.parseInt(s[1]) : DEFAULT_PORT;

									log.info("Setting DOSIMETER to " + host
											+ ":" + port);
									dosimeterClientHandler
											.connect(new InetSocketAddress(
													host, port));
								}
							}
						}
					});

			// Configure the client.
			ClientBootstrap bootstrap = new ClientBootstrap(
					new NioClientSocketChannelFactory(
							Executors.newCachedThreadPool(),
							Executors.newCachedThreadPool()));

			dosimeterClientHandler = new DosimeterClientHandler(bootstrap,
					eventBus);

			Timer timer = new HashedWheelTimer();

			// Configure the pipeline factory.
			bootstrap.setPipelineFactory(new PtuPipelineFactory(timer,
					dosimeterClientHandler));
		}
	}
}
