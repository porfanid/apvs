package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cedarsoftware.util.io.JsonWriter;

public class PtuJsonWriter extends JsonWriter {

	private DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public PtuJsonWriter(OutputStream out) throws IOException {
		super(out);
	}

	@Override
	public void write(Object obj) throws IOException {
		traceReferences(obj);
		_objVisited.clear();
		writeImpl(obj, false);
		flush();
		_objVisited.clear();
		_objsReferenced.clear();
	}

	@Override
    protected void writeDate(Object obj, boolean showType) throws IOException
    {
        String value = "\""+format.format((Date)obj)+"\"";

        if (showType)
        {
            _out.write('{');
            writeType(obj);
            _out.write(',');
            _out.write("\"value\":");
            _out.write(value);
            _out.write('}');
        }
        else
        {
            _out.write(value);
        }
    }
}
