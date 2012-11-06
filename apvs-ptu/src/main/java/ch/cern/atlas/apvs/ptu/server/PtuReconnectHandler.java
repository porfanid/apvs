package ch.cern.atlas.apvs.ptu.server;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PtuReconnectHandler extends IdleStateAwareChannelUpstreamHandler {
	private static final int RECONNECT_DELAY = 20;
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private ClientBootstrap bootstrap;

	private InetSocketAddress address;
	private Channel channel;
	private Timer reconnectTimer;
	private boolean reconnectNow;

	public PtuReconnectHandler(ClientBootstrap bootstrap) {
		this.bootstrap = bootstrap;		
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		log.info("Connected to PTU");
		channel = e.getChannel();
		super.channelConnected(ctx, e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		log.info("Disconnected from " + e.getChannel().getRemoteAddress());
		super.channelDisconnected(ctx, e);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// handle closed connection
		log.info("Closed PTU socket ");

		// handle (re)connection
		channel = null;
		if (reconnectNow) {
			log.info("Immediate Reconnecting to PTU on " + address);
			bootstrap.connect(address);
			reconnectNow = false;
		} else {
			log.info("Sleeping for: " + RECONNECT_DELAY + "s");
			reconnectTimer = new HashedWheelTimer();
			reconnectTimer.newTimeout(new TimerTask() {
				public void run(Timeout timeout) throws Exception {
					log.info("Reconnecting to PTU_DAQ on " + address);
					bootstrap.connect(address);
				}
			}, RECONNECT_DELAY, TimeUnit.SECONDS);
		}

		super.channelClosed(ctx, e);
	}
	
	@Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
        if (e.getState() == IdleState.READER_IDLE) {
            e.getChannel().close();
        } else if (e.getState() == IdleState.WRITER_IDLE) {
//            e.getChannel().write(new PingMessage());
        }
    }

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		if (e.getCause() instanceof ConnectException) {
			log.warn("Connection Refused");
		} else if (e.getCause() instanceof SocketException) {
			log.warn("Network is unreachable");
		} else {
			log.warn("Unexpected exception from downstream.", e.getCause());
		}
		e.getChannel().close();
	}

	public void connect(InetSocketAddress newAddress) {
		if (newAddress.equals(address))
			return;

		address = newAddress;

		if (channel != null) {
			reconnect(true);
		} else {
			bootstrap.connect(address);
		}
	}

	public void reconnect(boolean reconnectNow) {
		this.reconnectNow = reconnectNow;

		if (reconnectTimer != null) {
			reconnectTimer.stop();
			reconnectTimer = null;
		}

		if (channel != null) {
			channel.disconnect();
			channel = null;
		}
	}
	
	public boolean isConnected() {
		return channel != null && channel.isConnected();
	}

}
