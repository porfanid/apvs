package ch.cern.atlas.apvs.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageList;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.InputStream;
import java.util.Iterator;

import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.Packet;
import ch.cern.atlas.apvs.ptu.server.PtuJsonReader;

public class JsonMessageDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			MessageList<Object> out) throws Exception {
		System.err.println("YYY");

		try {
			InputStream is = new ByteBufInputStream(in);
			PtuJsonReader json = new PtuJsonReader(is, false);
			Packet result = (Packet) json.readObject();
			json.close();
			System.err.println("Parsed ok");
			System.err.println(result);
			for (Iterator<Message> i = result.getMessages().iterator(); i
					.hasNext();) {
				System.err.println(i.next());
			}
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
		// String s = in.readBytes(in.readableBytes());
		// System.err.println(s);
	}

}
