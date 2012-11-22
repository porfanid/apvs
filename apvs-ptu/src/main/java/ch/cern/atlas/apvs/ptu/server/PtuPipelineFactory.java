package ch.cern.atlas.apvs.ptu.server;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;

public class PtuPipelineFactory implements ChannelPipelineFactory {

	private final ChannelHandler handler;
	private final ChannelHandler idleStateHandler;

	public PtuPipelineFactory(Timer timer, ChannelHandler handler) {
		this.handler = handler;
		this.idleStateHandler = new IdleStateHandler(timer, 60, 30, 0); // timer
																		// must
																		// be
																		// shared.
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("idle", idleStateHandler);

		// Add the text line codec combination first,
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192,
				ChannelBuffers.wrappedBuffer(new byte[] { 0x13 })));
		pipeline.addLast("decoder", new StringDecoder());
		pipeline.addLast("encoder", new StringEncoder());

		// and then business logic.
		pipeline.addLast("handler", handler);

		return pipeline;
	}
}
