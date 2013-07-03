package ch.cern.atlas.apvs.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RemoveDelimiterDecoder extends ByteToMessageDecoder {
	
	public RemoveDelimiterDecoder() {
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			MessageList<Object> out) throws Exception {
	
		ByteBuf o = Unpooled.buffer();
		int len = in.readableBytes();
		for (int i = 0; i < len; i++) {
			byte b = in.readByte();
			if ((b != 0x10) && (b != 0x13)) {
				o.writeByte(b);
			}
		}
		
		out.add(o);
	}
}
