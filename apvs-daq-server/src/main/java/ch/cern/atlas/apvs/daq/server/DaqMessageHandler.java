package ch.cern.atlas.apvs.daq.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;


@Sharable
public class DaqMessageHandler extends SimpleChannelInboundHandler<String> {

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String line)
			throws Exception {
		System.err.println("DAQ IN "+line);
	}
}
