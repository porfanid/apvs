package ch.cern.atlas.apvs.daq.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.ptu.server.PtuChannelInitializer;

public class DaqServer {
//	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private int inPort;
	private int outPort;

	public DaqServer(int inPort, int outPort) {
		this.inPort = inPort;
		this.outPort = outPort;
	}

	public void run() {

		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup);
			bootstrap.channel(NioServerSocketChannel.class);

			bootstrap.childHandler(new PtuChannelInitializer(
					new DaqMessageHandler(), false));
			
			bootstrap.option(ChannelOption.SO_BACKLOG, 128);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture fin = bootstrap.bind(new InetSocketAddress(inPort))
					.sync();
			System.out.println("DaqServer in: " + inPort + " out: " + outPort);

			fin.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			System.err.println("Problem: " + e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		new DaqServer(10123, 10124).run();
	}
}
