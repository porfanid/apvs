package ch.cern.atlas.apvs.ptu.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.domain.Packet;

public class JsonMessageDecoder extends ReplayingDecoder<Void> {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (ctx.channel().isActive()) {
			ByteBufInputStream is = new ByteBufInputStream(in);
			// System.err.println("RIN/WIN: "+in.readerIndex()+" "+in.writerIndex());
			PtuJsonReader reader = new PtuJsonReader(is);
			JsonHeader header = (JsonHeader)reader.readObject();
			log.info("*** " + header);
			Packet packet = new Packet(header.getSender(), header.getReceiver(), header.getFrameId(), header.getAcknowledge());
			// FIXME add messages
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
