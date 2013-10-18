package ch.cern.atlas.apvs.daq.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.db.Database;
import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Message;
import ch.cern.atlas.apvs.domain.Packet;
import ch.cern.atlas.apvs.ptu.server.MessageEvent;

import com.google.gwt.event.shared.EventBus;

public class DatabaseWriter {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	public DatabaseWriter(EventBus bus) {
		final Database database = Database.getInstance();
		
		bus.addHandler(MessageEvent.TYPE, new MessageEvent.Handler() {

			@Override
			public void onMessageReceived(MessageEvent messageEvent) {
				if (messageEvent.getPrefix().equals("IN")) {
					Packet packet = messageEvent.getPacket();
					
					for (Message msg : packet.getMessages()) {
						switch (msg.getType()) {
						 	case "Measurement":
						 		Measurement measurement = (Measurement)msg;
						 		
						 		database.saveOrUpdate(measurement, false);
						 		log.info("Written to DB: "+measurement);
						 		
						 		break;
						 		
						 	case "Event":
						 		Event event = (Event)msg;
						 		
						 		database.saveOrUpdate(event, false);
						 		log.info("Written to DB: "+event);
						 		
						 		break;
						 	default:
						 		log.warn("Unhandled message type: "+msg);
						 		break;
						}
					}
				}
			}
		});
	}
}
