package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.PlaceChangedEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.tablet.CameraPlace;
import ch.cern.atlas.apvs.client.tablet.HomePlace;
import ch.cern.atlas.apvs.client.tablet.ImagePlace;
import ch.cern.atlas.apvs.client.tablet.ProcedurePlace;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceView extends SimplePanel {

	private RemoteEventBus remoteEventBus;
	private String defaultImage = "Default-640x480.jpg";
	private Integer ptuId;

	public PlaceView(ClientFactory clientFactory, RemoteEventBus localEventBus) {
		this(clientFactory, localEventBus, "100%", "100%");
	}

	public PlaceView(final ClientFactory clientFactory, final RemoteEventBus localEventBus, final String width, final String height) {
		this.remoteEventBus = clientFactory.getEventBus();
		
		SelectPtuEvent.subscribe(localEventBus, new SelectPtuEvent.Handler() {
			
			@Override
			public void onPtuSelected(SelectPtuEvent event) {
				ptuId = event.getPtuId();
				
				remoteEventBus.fireEvent(new RequestRemoteEvent(PlaceChangedEvent.class));
			}
		});

		PlaceChangedEvent.subscribe(remoteEventBus,
				new PlaceChangedEvent.Handler() {

					@Override
					public void onPlaceChanged(PlaceChangedEvent event) {
						if (ptuId == null) return;
						
						if (!ptuId.equals(event.getPtuId())) return;
						
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
							image.setWidth(width);
							setWidget(image);
							return;
						}
						
						if (place instanceof CameraPlace) {
							CameraPlace cameraPlace = (CameraPlace) place;
							// FIXME should be local event bus
							setWidget(new CameraView(remoteEventBus, localEventBus, cameraPlace.getType(), width, height));
							return;							
						}
						
						if (place instanceof ProcedurePlace) {
							ProcedurePlace procedurePlace = (ProcedurePlace)place;
							setWidget(clientFactory.getProcedureView(width, height, procedurePlace.getUrl(), procedurePlace.getName(), procedurePlace.getStep()));
							return;
						}
						
						Image image = new Image(defaultImage);
						image.setWidth(width);
						setWidget(image);
						return;
					}					
				});
		
		Image image = new Image(defaultImage);
		image.setWidth(width);
		setWidget(image);
	}
}
