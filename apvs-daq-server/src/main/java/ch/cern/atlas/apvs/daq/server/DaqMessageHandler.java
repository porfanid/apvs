package ch.cern.atlas.apvs.daq.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.ReplayingDecoder;
import ch.cern.atlas.apvs.ptu.server.PtuJsonReader;

public class DaqMessageHandler extends ReplayingDecoder<Void> {
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			MessageList<Object> out) throws Exception {
			ByteBufInputStream is = new ByteBufInputStream(in);
//			System.err.println("RIN/WIN: "+in.readerIndex()+" "+in.writerIndex());
   		    PtuJsonReader reader = new PtuJsonReader(is);
			System.err.println(reader.readObject());
			reader.close();		
	}
}
