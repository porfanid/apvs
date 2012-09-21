package ch.cern.atlas.apvs.ptu.server;

public class JsonMessage {

	public String limit(String value, int length) {
		StringBuilder buf = new StringBuilder(value);
		if (buf.length() > length) {
			buf.setLength(length);
		}

		return buf.toString();
	}

}
