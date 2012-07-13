package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.client.ClientFactory;

public class SingleTimeView extends TimeView {

	public SingleTimeView(ClientFactory clientFactory, int height,
			boolean export, String args) {
		super(clientFactory, height, export, args);
	}

}
