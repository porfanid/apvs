package ch.cern.atlas.apvs.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ServerConnector {

	public static void main(String[] args) throws InterruptedException {
		if (args.length < 4) {
			System.err
					.println("Usage: ServerConnector source-host source-port dest-host dest-port");
			System.exit(1);
		}

		String srcHost = args[0];
		int srcPort = Integer.parseInt(args[1]);
		String dstHost = args[2];
		int dstPort = Integer.parseInt(args[3]);
		
		System.out.println("ServerConnector");

		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new TimeClientHandler());
				}
			});

			// Start the client.
			ChannelFuture f = b.connect(srcHost, srcPort).sync();

			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
}