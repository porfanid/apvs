package ch.cern.atlas.apvs.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.domain.Packet;
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

	public VideoClientHandler(Bootstrap bootstrap, final RemoteEventBus eventBus)
			throws SerializationException {
		super(bootstrap, "VIDEO");
		this.eventBus = eventBus;

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

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
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ConnectionStatusChangedRemoteEvent.fire(eventBus, ConnectionType.video,
				false, getCause());
		super.channelInactive(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet packet)
			throws Exception {
		log.info("READ VIDEO " + packet);
	}
}
