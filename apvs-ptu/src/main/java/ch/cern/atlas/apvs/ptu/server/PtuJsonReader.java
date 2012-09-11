package ch.cern.atlas.apvs.ptu.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.cedarsoftware.util.io.JsonReader;

public class PtuJsonReader extends JsonReader {

	public PtuJsonReader(InputStream in) {
		super(in);
	}

	public PtuJsonReader(InputStream in, boolean noObjects) {
		super(in, noObjects);
	}

	protected Object createJavaObjectInstance(Class clazz, JsonObject jsonObj)
			throws IOException {
		System.err.println("XXX Reading "+jsonObj);
		
		String sender = (String)jsonObj.get("Sender");
		if (sender == null) {
			return super.createJavaObjectInstance(clazz, jsonObj);
		}
		String receiver = (String)jsonObj.get("Receiver");
		
		Header header = new Header();
		
		List messages = (List)jsonObj.get("Messages");
		
		System.err.println("Y"+jsonObj.get("Messages").getClass());
		System.err.println("Z"+messages.size());
		jsonObj.put("@type", "ch.cern.atlas.apvs.ptu.server.Header");
//		jsonObj.put("name", jsonObj.get("sensor"));
//		jsonObj.remove("sensor");
		return null;
	}

	@Override
	protected Date convertToDate(Object rhs) {
		try {
			return PtuConstants.dateFormat.parse((String) rhs);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Object jsonToJava(String json) throws IOException {
		ByteArrayInputStream ba = new ByteArrayInputStream(
				json.getBytes("UTF-8"));
		PtuJsonReader jr = new PtuJsonReader(ba, false);
		Object result = jr.readObject();
		jr.close();
		return result;
	}

	public static Object toJava(String json) {
		try {
			return jsonToJava(json);
		} catch (Exception ignored) {
			return null;
		}
	}
}
