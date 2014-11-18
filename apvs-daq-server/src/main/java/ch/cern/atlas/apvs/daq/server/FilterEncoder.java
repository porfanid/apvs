package ch.cern.atlas.apvs.daq.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.Packet;

public class FilterEncoder extends MessageToMessageEncoder<Packet> {
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Packet packet,
			List<Object> out) throws Exception {
		
		Packet outPacket = new Packet(packet.getSender(), packet.getReceiver(), packet.getFrameID(), packet.getAcknowledge());
		
		for (Iterator<Message> i=packet.getMessages().iterator(); i.hasNext(); ) {
			Message msg = i.next();
			if (msg.getType().equals("Measurement")) {
				Measurement measurement = (Measurement)msg;
				if (measurement.getSensor().equals("O2")) {
					outPacket.addMessage(measurement);
				}
			}
		}
		
		if (outPacket.getMessages().size() > 0) {
			out.add(outPacket);
		}
	}

}
