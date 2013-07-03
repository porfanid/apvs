package ch.cern.atlas.apvs.server;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import com.google.gwt.event.shared.EventBus;

@Sharable
public class MessageForwarder extends SimpleChannelInboundHandler<String> {

	private String prefix;
	private EventBus bus;
	private ChannelHandlerContext ctx;

	public MessageForwarder(final String prefix, EventBus bus) {
		this.prefix = prefix;
		this.bus = bus;

		bus.addHandler(MessageEvent.TYPE, new MessageEvent.Handler() {

			@Override
			public void onMessageReceived(MessageEvent event) {
				if (event.getPrefix().equals(prefix)) {
					return;
				}

				System.out.println("Will send to " + prefix);
				if ((ctx != null) && ctx.channel().isActive()) {
					ctx.write(event.getMessage());
				} else {
					System.err.println("Channel closed");
				}
			}
		});
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, String msg)
			throws Exception {

		System.err.println(prefix + " '" + msg + "'");
		bus.fireEvent(new MessageEvent(prefix, msg));
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		System.err.println(prefix + " active");
		this.ctx = ctx;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		System.err.println(prefix + " not active");

		this.ctx = ctx;
	}
}
