package ch.cern.atlas.apvs.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.domain.Intervention;
import ch.cern.atlas.apvs.domain.Packet;
import ch.cern.atlas.apvs.domain.VideoStartStop;
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
			sendCommand(intervention, intervention.getRecStatus() == 0);
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
	
	public void sendCommand(final Intervention intervention, final boolean start) {
		if (ctx.channel().isActive()) {
			Packet packet = new Packet("Server", 4000, false, new VideoStartStop(intervention, start));
			ctx.channel().write(packet).addListener(new GenericFutureListener<Future<? super Void>>() {

				@Override
				public void operationComplete(Future<? super Void> future)
						throws Exception {
					if (future.isSuccess()) {
						int recStatus = start ? 1 : 0;
						log.info("Updated rec-status for "+intervention.getName()+" to "+recStatus);
						intervention.setRecStatus(recStatus);
						database.saveOrUpdate(intervention);
					} else {
						log.error("ERROR: "
								+ future.cause());
					}
				}
			});
			ctx.flush();
		}
	}
}
