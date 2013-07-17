package ch.cern.atlas.apvs.ptu.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.ReplayingDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonMessageDecoder extends ReplayingDecoder<Void> {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			MessageList<Object> out) throws Exception {
		if (ctx.channel().isActive()) {
			ByteBufInputStream is = new ByteBufInputStream(in);
			// System.err.println("RIN/WIN: "+in.readerIndex()+" "+in.writerIndex());
			PtuJsonReader reader = new PtuJsonReader(is);
			Object packet = reader.readObject();
			log.info("*** " + packet);
			out.add(packet);
			reader.close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		// super.exceptionCaught(ctx, cause);

		log.trace("EX: ", cause);
	}
}
