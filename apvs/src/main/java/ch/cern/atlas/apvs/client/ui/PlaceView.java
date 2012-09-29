package ch.cern.atlas.apvs.client.ui;

import java.util.logging.Logger;

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
import com.google.web.bindery.event.shared.EventBus;

public class PlaceView extends SimplePanel {

	private final Logger log = Logger.getLogger(getClass().getName());
	
	private RemoteEventBus remoteEventBus;
	private String defaultImage = "Default-640x480.jpg";
	private String ptuId;

	public PlaceView(ClientFactory clientFactory, Arguments args) {
		this(clientFactory, args, "100%", "100%");
	}

	public PlaceView(final ClientFactory clientFactory, final Arguments args,
			final String width, final String height) {
		this.remoteEventBus = clientFactory.getRemoteEventBus();

		EventBus eventBus = clientFactory.getEventBus(args.getArg(0));

		if (eventBus != null) {
			SelectPtuEvent.subscribe(eventBus, new SelectPtuEvent.Handler() {

				@Override
				public void onPtuSelected(SelectPtuEvent event) {
					ptuId = event.getPtuId();

					remoteEventBus.fireEvent(new RequestRemoteEvent(
							PlaceChangedEvent.class));
				}
			});
		}

		PlaceChangedEvent.subscribe(remoteEventBus,
				new PlaceChangedEvent.Handler() {

					@Override
					public void onPlaceChanged(PlaceChangedEvent event) {
						if (ptuId == null)
							return;

						if (!ptuId.equals(event.getPtuId()))
							return;

						log.info("PLACE CHANGED " + event);
						Place place = event.getPlace();

						if (place instanceof HomePlace) {
							Image image = new Image(defaultImage);
							image.setWidth(width + "" + Unit.PX);
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
							setWidget(new CameraView(clientFactory, cameraPlace
									.getType(), width, height));
							return;
						}

						if (place instanceof ProcedurePlace) {
							ProcedurePlace procedurePlace = (ProcedurePlace) place;
							setWidget(clientFactory.getProcedureView(width,
									height, procedurePlace.getUrl(),
									procedurePlace.getName(),
									procedurePlace.getStep()));
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
