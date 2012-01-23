package ch.cern.atlas.apvs.ptu.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;

import ch.cern.atlas.apvs.domain.Ptu;

import com.cedarsoftware.util.io.JsonWriter;

public class PtuJsonWriter extends JsonWriter implements ObjectWriter {

	public PtuJsonWriter(OutputStream out) throws IOException {
		super(out);
	}

	@Override
	protected void writeImpl(Object obj, boolean showType) throws IOException {
		if (obj instanceof Boolean || obj instanceof Long
				|| obj instanceof Double) {
			writePrimitive(obj);
		} else {
			super.writeImpl(obj, false);
		}
	}

	@Override
	protected void writeFieldName(String name) throws IOException {
		if (name.equals("name")) {
			write("\"sensor\"");
		} else {
			super.writeFieldName(name);
		}
	}

	@Override
	protected void writeDate(Object obj, boolean showType) throws IOException {
		String value = "\"" + PtuConstants.dateFormat.format((Date) obj) + "\"";

		if (showType) {
			_out.write('{');
			writeType(obj);
			_out.write(',');
			_out.write("\"value\":");
			_out.write(value);
			_out.write('}');
		} else {
			_out.write(value);
		}
	}

	@Override
	public void newLine() throws IOException {
		_out.append("\n");
	}

	@Override
	public void write(Ptu ptu) throws IOException {
		for (Iterator<String> i = ptu.getMeasurementNames().iterator(); i
				.hasNext();) {
			String name = i.next();
			write(ptu.getMeasurement(name));
			newLine();
		}
	}

	public static String toJson(Object item) {
		try {
			return objectToJson(item);
		} catch (IOException ignored) {
			return null;
		}
	}

	public static String objectToJson(Object item) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PtuJsonWriter writer = new PtuJsonWriter(stream);
		writer.write(item);
		writer.close();
		return new String(stream.toByteArray(), "UTF-8");
	}

}
