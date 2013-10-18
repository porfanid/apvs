package ch.cern.atlas.apvs.ptu.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.domain.Packet;

public abstract class PtuReconnectHandler extends SimpleChannelInboundHandler<Packet> {
	private static final int RECONNECT_DELAY = 20;
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private Bootstrap bootstrap;

	private InetSocketAddress address;
	private Channel channel;
	private Timer reconnectTimer;
	private boolean reconnectNow;
	
	private String cause;

	public PtuReconnectHandler(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("Connected to PTU");
		channel = ctx.channel();
		cause = "";
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
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

		super.channelInactive(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleState) {
			IdleState e = (IdleState) evt;
			if (e == IdleState.READER_IDLE) {
				ctx.channel().close();
			} else if (e == IdleState.WRITER_IDLE) {
				// ctx.channel().write(new PingMessage());
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable caught) {
		if (caught instanceof ConnectException) {
			cause = "Connection Refused";
			log.warn(cause);
		} else if (caught instanceof SocketException) {
			cause = "Network is unreachable";
			log.warn(cause);
		} else {
			cause = caught.getMessage();
			log.warn("Unexpected exception from downstream.", caught);
		}
		ctx.channel().close();
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
		return channel != null && channel.isActive();
	}
	
	public String getCause() {
		return cause;
	}

	public Channel getChannel() {
		return channel;
	}

}
