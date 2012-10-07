package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.client.ClientFactory;

public interface Module {

	public void configure(String id, ClientFactory clientFactory, Arguments args);
}
