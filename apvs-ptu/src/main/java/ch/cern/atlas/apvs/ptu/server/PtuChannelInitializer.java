package ch.cern.atlas.apvs.ptu.server;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;

import ch.cern.atlas.apvs.domain.Device;

public class PtuChannelInitializer extends ChannelInitializer<SocketChannel> {

	private ChannelInboundHandlerAdapter handler;
	private Map<String, Device> devices;

	public PtuChannelInitializer(ChannelInboundHandlerAdapter handler, Map<String, Device> devices) {
		this.handler = handler;
		this.devices = devices;
	}

//	@Override
//	protected void initChannel(SocketChannel ch) throws Exception {
//		ch.pipeline().addLast(new IdleStateHandler(60, 30, 0));
//		if (delimiter) {
//			ch.pipeline().addLast(
//					new DelimiterBasedFrameDecoder(8192, Unpooled
//							.wrappedBuffer(new byte[] { 0x13 })));
//		}
//		ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
//		ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
//		ch.pipeline().addLast(handler);
//	}
	
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new IdleStateHandler(60, 30, 0));
		if (devices != null) {
			ch.pipeline().addLast(new RemoveDelimiterDecoder());
			ch.pipeline().addLast(new JsonMessageDecoder(devices));
		}
		ch.pipeline().addLast(new JsonMessageEncoder());
		if (devices != null) {
			ch.pipeline().addLast(handler);
		}
	}
}
