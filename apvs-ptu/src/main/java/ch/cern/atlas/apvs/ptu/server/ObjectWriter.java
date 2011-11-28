package ch.cern.atlas.apvs.ptu.server;

import java.io.IOException;

import ch.cern.atlas.apvs.domain.Ptu;

public interface ObjectWriter {

	public void write(Object obj) throws IOException;
	
	public void write(Ptu ptu) throws IOException;

	public void flush() throws IOException;

	public void close() throws IOException;

	public void newLine() throws IOException;
}
