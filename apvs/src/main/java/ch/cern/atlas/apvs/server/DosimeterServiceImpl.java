package ch.cern.atlas.apvs.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.dosimeter.server.DosimeterClientHandler;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.server.PtuClientPipelineFactory;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class DosimeterServiceImpl extends ResponsePollService {

	private static final int DEFAULT_PORT = 4001;
	
	private String dosimeterUrl;
	
	private DosimeterClientHandler dosimeterClientHandler;
	private RemoteEventBus eventBus;

	public DosimeterServiceImpl() {
		System.out.println("Creating DosimeterService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("Starting DosimeterService...");
		
		ServerSettingsChangedEvent.subscribe(eventBus, new ServerSettingsChangedEvent.Handler() {
			
			@Override
			public void onServerSettingsChanged(ServerSettingsChangedEvent event) {
				ServerSettings settings = event.getServerSettings();
				if (settings != null) {
					String url = settings.get(ServerSettings.settingNames[1]);
					if ((url != null) && !url.equals(dosimeterUrl)) {
						dosimeterUrl = url;
						String[] s = dosimeterUrl.split(":", 2);
						String host = s[0];
						int port = s.length > 1 ? Integer.parseInt(s[1]) : DEFAULT_PORT;
						
						System.err.println("Setting DOSIMETER to " + host
								+ ":" + port);
						dosimeterClientHandler.connect(new InetSocketAddress(
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

		dosimeterClientHandler = new DosimeterClientHandler(bootstrap, eventBus);

		// Configure the pipeline factory.
		bootstrap.setPipelineFactory(new PtuClientPipelineFactory(
				dosimeterClientHandler));
	}
}
