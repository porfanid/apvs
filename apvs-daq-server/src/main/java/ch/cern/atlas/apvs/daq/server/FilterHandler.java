package ch.cern.atlas.apvs.daq.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.ptu.server.JsonMessageDecoder;
import ch.cern.atlas.apvs.ptu.server.JsonMessageEncoder;
import ch.cern.atlas.apvs.ptu.server.MessageToBus;
import ch.cern.atlas.apvs.ptu.server.RemoveDelimiterDecoder;

import com.google.gwt.event.shared.EventBus;

public class FilterHandler {
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	public FilterHandler(final EventBus bus, final Map<String, Device> devices,
			int outPort) {
		EventLoopGroup boutGroup = new NioEventLoopGroup();
		EventLoopGroup woutGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap bout = new ServerBootstrap();
			bout.group(boutGroup, woutGroup);
			bout.channel(NioServerSocketChannel.class);
	
			bout.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new IdleStateHandler(60, 30, 0));
					ch.pipeline().addLast(new RemoveDelimiterDecoder());
					ch.pipeline().addLast(new JsonMessageDecoder(devices));
					ch.pipeline().addLast(new JsonMessageEncoder());
					ch.pipeline().addLast(new FilterEncoder());
					ch.pipeline().addLast(new MessageToBus("OUT", bus));
				}
			});
	
			bout.option(ChannelOption.SO_BACKLOG, 128);
			bout.childOption(ChannelOption.SO_KEEPALIVE, true);
	
			final ChannelFuture fout = bout.bind(new InetSocketAddress(outPort))
					.sync();
	
			log.info("FilterServer out: " + outPort);

			fout.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error("EX: " + e);
			e.printStackTrace();
		} finally {
			woutGroup.shutdownGracefully();
			boutGroup.shutdownGracefully();
		}
	}
}
