package ch.cern.atlas.apvs.ptu.server;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

public class PtuReconnectHandler extends SimpleChannelUpstreamHandler {
	private static final int RECONNECT_DELAY = 20;
	private final Logger log = Logger.getLogger(getClass().getName());

	private ClientBootstrap bootstrap;

	private InetSocketAddress address;
	private Channel channel;
	private Timer timer;
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
		log.info("Disconnected from "
				+ e.getChannel().getRemoteAddress());
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
			timer = new HashedWheelTimer();
			timer.newTimeout(new TimerTask() {
				public void run(Timeout timeout) throws Exception {
					log.info("Reconnecting to PTU_DAQ on " + address);
					bootstrap.connect(address);
				}
			}, RECONNECT_DELAY, TimeUnit.SECONDS);
		}

		super.channelClosed(ctx, e);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		if (e.getCause() instanceof ConnectException) {
			log.log(Level.WARNING, "Connection Refused");
		} else if (e.getCause() instanceof SocketException) {
			log.log(Level.WARNING, "Network is unreachable");
		} else {
			log.log(Level.WARNING, "Unexpected exception from downstream.",
					e.getCause());
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

		if (timer != null) {
			timer.stop();
			timer = null;
		}

		if (channel != null) {
			channel.disconnect();
			channel = null;
		}
	}

}
