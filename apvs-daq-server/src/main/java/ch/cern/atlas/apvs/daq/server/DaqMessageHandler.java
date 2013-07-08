package ch.cern.atlas.apvs.daq.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.ReplayingDecoder;


public class DaqMessageHandler extends ReplayingDecoder<Void> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			MessageList<Object> out) throws Exception {
		try {
		ByteBufInputStream is = new ByteBufInputStream(in);
		System.err.println("Decoding "+in.readerIndex()+" "+in.writerIndex());
		byte[] b = new byte[80];
		is.readFully(b);
		System.err.println(new String(b));
//		PtuJsonReader reader = new PtuJsonReader(is);
//		System.err.println(reader.readObject());
//		reader.close();
		is.close();
		System.err.println("Decoding Done");
		} catch (Exception e) {
			System.err.println(e);
		}
	}

//	@Override
//	protected void messageReceived(ChannelHandlerContext ctx, String line)
//			throws Exception {
//		System.err.println("DAQ IN "+line);
//	}
}
