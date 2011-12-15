package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.NavigateStepEvent;
import ch.cern.atlas.apvs.client.event.PlaceChangedEvent;
import ch.cern.atlas.apvs.client.event.SelectStepEvent;
import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.StepStatusEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceView extends SimplePanel {

	private RemoteEventBus remoteEventBus;

	public PlaceView(RemoteEventBus remoteEventBus) {
		this(remoteEventBus, 350, 300);
	}

	public PlaceView(final RemoteEventBus remoteEventBus, int width, int height) {
		this.remoteEventBus = remoteEventBus;
		
		PlaceChangedEvent.subscribe(remoteEventBus, new PlaceChangedEvent.Handler() {
			
			@Override
			public void onPlaceChanged(PlaceChangedEvent event) {
				System.out.println("PLACE CHANGED "+event);
			}
		});
	}
}
