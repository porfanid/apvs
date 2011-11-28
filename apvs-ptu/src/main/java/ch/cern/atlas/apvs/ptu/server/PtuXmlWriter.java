package ch.cern.atlas.apvs.ptu.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;

public class PtuXmlWriter extends Writer implements ObjectWriter {

	private BufferedWriter _out;

	public PtuXmlWriter(OutputStream out) throws IOException {
		try {
			_out = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IOException(
					"Unsupported encoding.  Get a JVM that supports UTF-8", e);
		}
	}

	@Override
	public void write(Object obj) throws IOException {
		if (obj instanceof Measurement<?>) {
			Measurement<?> m = (Measurement<?>) obj;
			String i = "    ";
			String newLine = "\n";
			_out.write(i);
			_out.write("<message type=\"measurement\">");
			_out.write(newLine);
			_out.write(i);
			_out.write(i);
			_out.write("<field name=\"ptu_id\">");
			_out.write(""+m.getPtuId());
			_out.write("</field>");
			_out.write(newLine);
			_out.write(i);
			_out.write(i);
			_out.write("<field name=\"sensor\">");
			_out.write(m.getName());
			_out.write("</field>");
			_out.write(newLine);
			_out.write(i);
			_out.write(i);
			_out.write("<field name=\"value\">");
			_out.write(""+m.getValue());
			_out.write("</field>");
			_out.write(newLine);
			_out.write(i);
			_out.write(i);
			_out.write("<field name=\"unit\">");
			_out.write(m.getUnit());
			_out.write("</field>");
			_out.write(newLine);
			_out.write(i);
			_out.write(i);
			_out.write("<field name=\"datetime\">");
			_out.write(PtuConstants.dateFormat.format(m.getDate()));
			_out.write("</field>");
			_out.write(newLine);
			_out.write(i);
			_out.write(i);
			_out.write("<field name=\"type\">");
			_out.write(m.getType());
			_out.write("</field>");
			_out.write(newLine);
			_out.write(i);
			_out.write("</message>");
			flush();
		}
	}

	@Override
	public void newLine() throws IOException {
		_out.append("\n");
		flush();
	}

	@Override
	public void close() throws IOException {
		_out.close();
	}

	@Override
	public void flush() throws IOException {
		_out.flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		_out.write(cbuf, off, len);
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
}
