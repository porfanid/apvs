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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.ptu.server.JsonMessageDecoder;
import ch.cern.atlas.apvs.ptu.server.JsonMessageEncoder;
import ch.cern.atlas.apvs.ptu.server.MessageEvent;
import ch.cern.atlas.apvs.ptu.server.MessageToBus;
import ch.cern.atlas.apvs.ptu.server.RemoveDelimiterDecoder;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class DaqServer {
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private int inPort;
	private int outPort;

	public DaqServer(int inPort, int outPort) {
		this.inPort = inPort;
		this.outPort = outPort;
	}

	public void run() {

		final EventBus bus = new SimpleEventBus();
		
		// Configure the server.
		EventLoopGroup binGroup = new NioEventLoopGroup();
		EventLoopGroup boutGroup = new NioEventLoopGroup();
		EventLoopGroup winGroup = new NioEventLoopGroup();
		EventLoopGroup woutGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bin = new ServerBootstrap();
			bin.group(binGroup, winGroup);
			bin.channel(NioServerSocketChannel.class);

			bin.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new IdleStateHandler(60, 30, 0));
					ch.pipeline().addLast(new RemoveDelimiterDecoder());
					ch.pipeline().addLast(new JsonMessageDecoder());
					ch.pipeline().addLast(new JsonMessageEncoder());
					ch.pipeline().addLast(new MessageToBus("IN", bus));
				}
			});

			bin.option(ChannelOption.SO_BACKLOG, 128);
			bin.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture fin = bin.bind(new InetSocketAddress(inPort))
					.sync();

			ServerBootstrap bout = new ServerBootstrap();
			bout.group(boutGroup, woutGroup);
			bout.channel(NioServerSocketChannel.class);

			bout.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new IdleStateHandler(60, 30, 0));
					ch.pipeline().addLast(new RemoveDelimiterDecoder());
					ch.pipeline().addLast(new JsonMessageDecoder());
					ch.pipeline().addLast(new JsonMessageEncoder());
					ch.pipeline().addLast(new MessageToBus("OUT", bus));
				}
			});

			bout.option(ChannelOption.SO_BACKLOG, 128);
			bout.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture fout = bout.bind(new InetSocketAddress(outPort))
					.sync();

			log.info("DaqServer in: " + inPort + " out: " + outPort);

			// debug the bus...
			bus.addHandler(MessageEvent.TYPE, new MessageEvent.Handler() {
				
				@Override
				public void onMessageReceived(MessageEvent event) {
					log.info(""+event);
				}
			});
			
			fin.channel().closeFuture().sync();
			fout.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error("EX: " + e);
			e.printStackTrace();
		} finally {
			winGroup.shutdownGracefully();
			binGroup.shutdownGracefully();
			woutGroup.shutdownGracefully();
			boutGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		if ((args.length != 0) && (args.length != 2)) {
			System.err
					.println("Usage: DaqServer source-port dest-port");
			System.exit(1);
		}
		
		new DaqServer(args.length > 0 ? Integer.parseInt(args[0]) : 10123, args.length > 1 ? Integer.parseInt(args[1]) : 10124).run();
	}
}
