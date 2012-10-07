package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

public class AudioView extends SimplePanel implements Module {

	private RemoteEventBus remoteEventBus;

	public AudioView() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory, Arguments args) {
		this.remoteEventBus = clientFactory.getRemoteEventBus();
		
		EventBus eventBus = clientFactory.getEventBus(args.getArg(0));

		add (new Button("Audio goes here !!"));
		
		return true;
	}
}
