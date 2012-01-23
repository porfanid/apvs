package ch.cern.atlas.apvs.ptu.daq.server;

import java.io.IOException;

public interface PtuDaqListener {

	public void itemAvailable(String item) throws IOException;
}
