package ch.cern.atlas.apvs.server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.service.PtuService;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.server.PtuClientHandler;
import ch.cern.atlas.apvs.ptu.server.PtuPipelineFactory;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class PtuServiceImpl extends ResponsePollService implements PtuService {

	private static final int DEFAULT_PORT = 4005;

	private String ptuUrl;

	private RemoteEventBus eventBus;
	private PtuClientHandler ptuClientHandler;

	public PtuServiceImpl() {
		System.out.println("Creating PtuService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		System.out.println("Starting PtuService...");

		ServerSettingsChangedEvent.subscribe(eventBus,
				new ServerSettingsChangedEvent.Handler() {

					@Override
					public void onServerSettingsChanged(
							ServerSettingsChangedEvent event) {
						ServerSettings settings = event.getServerSettings();
						if (settings != null) {
							String url = settings
									.get(ServerSettings.settingNames[0]);
							if ((url != null) && !url.equals(ptuUrl)) {
								ptuUrl = url;
								String[] s = ptuUrl.split(":", 2);
								String host = s[0];
								int port = s.length > 1 ? Integer
										.parseInt(s[1]) : DEFAULT_PORT;

								System.err.println("Setting PTU to " + host
										+ ":" + port);
								ptuClientHandler.connect(new InetSocketAddress(
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

		ptuClientHandler = new PtuClientHandler(bootstrap, eventBus);

		// Configure the pipeline factory.
		bootstrap.setPipelineFactory(new PtuPipelineFactory(
				ptuClientHandler));
	}

	@Override
	public Ptu getPtu(int ptuId) {
		return ptuClientHandler != null ? ptuClientHandler.getPtu(ptuId) : null;
	}

	@Override
	public List<Measurement<Double>> getMeasurements(int ptuId, String name) {
		Ptu ptu = getPtu(ptuId);

		return ptu != null ? ptu.getMeasurements(name) : null;
	}

	public Map<Integer, List<Measurement<Double>>> getMeasurements(String name) {
		Map<Integer, List<Measurement<Double>>> result = new HashMap<Integer, List<Measurement<Double>>>();
		if (ptuClientHandler == null)
			return result;

		for (Iterator<Integer> i = ptuClientHandler.getPtuIds().iterator(); i
				.hasNext();) {
			int ptuId = i.next();
			Ptu ptu = ptuClientHandler.getPtu(ptuId);
			if (ptu != null) {
				result.put(ptuId, ptu.getMeasurements(name));
			}
		}
		return result;
	}
}
