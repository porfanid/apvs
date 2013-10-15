package ch.cern.atlas.apvs.ptu.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.domain.Packet;

public class JsonMessageEncoder extends MessageToByteEncoder<Packet> {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	public JsonMessageEncoder() {
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out)
			throws Exception {
		ByteBuf encoded = Unpooled.buffer();
		ByteBufOutputStream os = new ByteBufOutputStream(encoded);
		PtuJsonWriter writer = new PtuJsonWriter(os);		
		writer.write(packet);
		writer.close();
		os.close();
		
		log.info("Encoded  "+encoded.toString(CharsetUtil.UTF_8));
		out.writeBytes(encoded);
		
		ctx.flush();
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		
		promise.addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future)
					throws Exception {
				System.err.println("Future "+future);
			}
		});

			super.write(ctx, msg, promise);
	}
}
