package ch.cern.atlas.apvs.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

import ch.cern.atlas.apvs.ptu.server.PtuChannelInitializer;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

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

		EventBus bus = new SimpleEventBus();
		
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		Bootstrap bin = new Bootstrap();
		bin.group(workerGroup);
		bin.channel(NioSocketChannel.class);
		bin.option(ChannelOption.SO_KEEPALIVE, true);
		final MessageForwarder fin = new MessageForwarder("IN", bus);
		bin.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new IdleStateHandler(60, 30, 0));
				ch.pipeline().addLast(new RemoveDelimiterDecoder());
				ch.pipeline().addLast(new JsonMessageDecoder());
				ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
				ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
				ch.pipeline().addLast(fin);
			}
		});

		Bootstrap bout = new Bootstrap();
		bout.group(workerGroup);
		bout.channel(NioSocketChannel.class);
		bout.option(ChannelOption.SO_KEEPALIVE, true);
		final MessageForwarder fout = new MessageForwarder("OUT", bus);
		bout.handler(new PtuChannelInitializer(fout, false));

		bout.connect(new InetSocketAddress(dstHost, dstPort));	
		bin.connect(new InetSocketAddress(srcHost, srcPort));
	}
}