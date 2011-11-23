package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;

public interface ObjectWriter {

	public void write(Object obj) throws IOException;

	public void flush() throws IOException;

	public void close() throws IOException;

	public void newLine() throws IOException;
}
