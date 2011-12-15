package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.PlaceChangedEvent;
import ch.cern.atlas.apvs.client.tablet.CameraPlace;
import ch.cern.atlas.apvs.client.tablet.HomePlace;
import ch.cern.atlas.apvs.client.tablet.ImagePlace;
import ch.cern.atlas.apvs.client.tablet.ProcedurePlace;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceView extends SimplePanel {

	private ClientFactory clientFactory;
	private RemoteEventBus remoteEventBus;
	private String defaultImage = "Default-640x480.jpg";

	public PlaceView(ClientFactory clientFactory) {
		this(clientFactory, 350, 300);
	}

	public PlaceView(final ClientFactory clientFactory, final int width, final int height) {
		this.clientFactory = clientFactory;
		this.remoteEventBus = clientFactory.getEventBus();

		PlaceChangedEvent.subscribe(remoteEventBus,
				new PlaceChangedEvent.Handler() {

					@Override
					public void onPlaceChanged(PlaceChangedEvent event) {
						System.out.println("PLACE CHANGED " + event);
						Place place = event.getPlace();

						if (place instanceof HomePlace) {
							Image image = new Image(defaultImage);
							image.setWidth(width+""+Unit.PX);
							setWidget(image);
							return;
						}
						
						if (place instanceof ImagePlace) {
							ImagePlace imagePlace = (ImagePlace) place;
							Image image = new Image(imagePlace.getUrl());
							image.setWidth(width+""+Unit.PX);
							setWidget(image);
							return;
						}
						
						if (place instanceof CameraPlace) {
							CameraPlace cameraPlace = (CameraPlace) place;
							// FIXME should be local event bus
							setWidget(new CameraView(remoteEventBus, remoteEventBus, cameraPlace.getType(), width, height));
							return;							
						}
						
						if (place instanceof ProcedurePlace) {
							ProcedurePlace procedurePlace = (ProcedurePlace)place;
// FIXME need to get step, url, ...
							setWidget(clientFactory.getProcedureView(width, height));
						}
					}					
				});
	}
}
