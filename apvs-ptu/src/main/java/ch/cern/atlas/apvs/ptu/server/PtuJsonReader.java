package ch.cern.atlas.apvs.ptu.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Message;

import com.cedarsoftware.util.io.JsonReader;

public class PtuJsonReader extends JsonReader {

	private static final Logger log = Logger.getLogger(PtuJsonReader.class
			.getName());

	public PtuJsonReader(InputStream in) {
		super(in);
	}

	public PtuJsonReader(InputStream in, boolean noObjects) {
		super(in, noObjects);
	}

	@Override
	public Object readObject() throws IOException {
		List<Message> result = new ArrayList<Message>();

		@SuppressWarnings("rawtypes")
		JsonObject jsonObj = (JsonObject) readIntoJsonMaps();

		String sender = (String) jsonObj.get("Sender");
		// String receiver = (String) jsonObj.get("Receiver");
		// String frameID = (String) jsonObj.get("FrameID");
		// String acknowledge = (String) jsonObj.get("Acknowledge");

		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<JsonObject> msgs = (List<JsonObject>) jsonObj.get("Messages");
		JsonMessage[] messages = new JsonMessage[msgs.size()];

		for (int i = 0; i < messages.length; i++) {
			@SuppressWarnings("rawtypes")
			JsonObject msg = msgs.get(i);
			String type = (String) msg.get("Type");
			if (type.equals("Measurement")) {
				result.add(new Measurement(sender, (String) msg.get("Sensor"),
						Double.parseDouble((String) msg.get("Value")),
						(String) msg.get("Unit"),
						convertToDate(msg.get("Time"))));
			} else if (type.equals("Event")) {
				result.add(new Event(sender, (String) msg.get("Sensor"),
						(String) msg.get("EventType"), Double
								.parseDouble((String) msg.get("Value")), Double
								.parseDouble((String) msg.get("Threshold")),
						convertToDate(msg.get("Time"))));
			} else {
				log.warning("Message type not implemented: " + type);
			}
			// FIXME add other types of messages, #115 #112 #114
		}

		// returns a list of messages
		return result;
	}

	@Override
	protected Date convertToDate(Object rhs) {
		try {
			return PtuServerConstants.dateFormat.parse((String) rhs);
		} catch (ParseException e) {
			return null;
		}
	}

	public static List<Message> jsonToJava(String json) throws IOException {
		ByteArrayInputStream ba = new ByteArrayInputStream(
				json.getBytes("UTF-8"));
		PtuJsonReader jr = new PtuJsonReader(ba, false);
		@SuppressWarnings("unchecked")
		List<Message> result = (List<Message>) jr.readObject();
		jr.close();
		return result;
	}

	public static List<Message> toJava(String json) {
		try {
			return jsonToJava(json);
		} catch (Exception ignored) {
			return null;
		}
	}
}
