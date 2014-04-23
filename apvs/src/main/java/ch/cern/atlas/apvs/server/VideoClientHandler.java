package ch.cern.atlas.apvs.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Intervention;
import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.Packet;
import ch.cern.atlas.apvs.domain.StartVideo;
import ch.cern.atlas.apvs.event.ConnectionStatusChangedRemoteEvent;
import ch.cern.atlas.apvs.event.ConnectionStatusChangedRemoteEvent.ConnectionType;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.server.PtuReconnectHandler;

import com.google.gwt.user.client.rpc.SerializationException;

@Sharable
public class VideoClientHandler extends PtuReconnectHandler {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	private final RemoteEventBus eventBus;
	private Database database;
	private ChannelHandlerContext ctx;

	public VideoClientHandler(Bootstrap bootstrap, final RemoteEventBus eventBus)
			throws SerializationException {
		super(bootstrap, "VIDEO");
		this.eventBus = eventBus;
		database = Database.getInstance();

		RequestRemoteEvent.register(this, eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				String type = event.getRequestedClassName();

				if (type.equals(ConnectionStatusChangedRemoteEvent.class
						.getName())) {
					ConnectionStatusChangedRemoteEvent.fire(eventBus,
							ConnectionType.video, isConnected(), getCause());
				}
			}
		});
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ConnectionStatusChangedRemoteEvent.fire(eventBus, ConnectionType.video,
				true, "");
		super.channelActive(ctx);
		this.ctx = ctx;
		
		// verify in the DB all open/closed interventions that have rec_status off/on respectively and correct.
		log.info("Checking open/closed videos");
		for (Intervention intervention : database.getInterventions()) {
			if (intervention.getRecStatus() > 0) {
				stopVideo(intervention);
			} else {
				startVideo(intervention);
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ConnectionStatusChangedRemoteEvent.fire(eventBus, ConnectionType.video,
				false, getCause());
		super.channelInactive(ctx);
		this.ctx = ctx;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet packet)
			throws Exception {
		log.info("READ VIDEO " + packet);
	}
	
	public void startVideo(Intervention intervention) {
		log.info("Start VIDEO for "+intervention);
		if (ctx.channel().isActive()) {
			Packet packet = new Packet("Server", 4000, false, new StartVideo(null));
		}
	}
	
	public void stopVideo(Intervention intervention) {
		log.info("Stopping VIDEO for "+intervention);	
	}
}
