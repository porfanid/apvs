package ch.cern.atlas.apvs.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.event.ServerSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.service.VideoService;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.server.PtuChannelInitializer;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * @author Mark Donszelmann
 */
@SuppressWarnings("serial")
public class VideoServiceImpl extends ResponsePollService implements VideoService {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private static final int DEFAULT_VIDEO_PORT = 20000;

	private String videoUrl;

	private RemoteEventBus eventBus;
	private VideoClientHandler videoClientHandler;

	public VideoServiceImpl() throws SerializationException {
		log.info("Creating VideoService...");
		eventBus = APVSServerFactory.getInstance().getEventBus();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		log.info("Starting VideoService...");

		Map<String, Device> devices = Database.getInstance().getDeviceMap();

		EventLoopGroup group = new NioEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);

		try {
			videoClientHandler = new VideoClientHandler(bootstrap, eventBus);
		} catch (SerializationException e) {
			throw new ServletException(e);
		}

		// Configure the pipeline factory.
		bootstrap.group(group);
		bootstrap.handler(new PtuChannelInitializer(videoClientHandler, devices, false));

		ServerSettingsChangedRemoteEvent.subscribe(eventBus,
				new ServerSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onServerSettingsChanged(
							ServerSettingsChangedRemoteEvent event) {
						ServerSettings settings = event.getServerSettings();
						if (settings != null) {
							String url = settings
									.get(ServerSettings.Entry.videoUrl.toString());
							if ((url != null) && !url.equals(videoUrl)) {
								videoUrl = url;
								String[] s = videoUrl.split(":", 2);
								String host = s[0];
								int port = s.length > 1 ? Integer
										.parseInt(s[1]) : DEFAULT_VIDEO_PORT;

								log.info("Setting VIDEO to " + host + ":" + port);
								videoClientHandler.connect(new InetSocketAddress(
										host, port));
							}
						}
					}
				});

	}
}
