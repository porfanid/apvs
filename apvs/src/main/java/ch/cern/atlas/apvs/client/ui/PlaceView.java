package ch.cern.atlas.apvs.client.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.PlaceChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.event.SwitchWidgetEvent;
import ch.cern.atlas.apvs.client.widget.IsSwitchableWidget;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

public class PlaceView extends SimplePanel implements Module,
		IsSwitchableWidget {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private RemoteEventBus remoteEventBus;
	private String defaultImage = "Default-640x480.jpg";
	private String ptuId;
	private boolean switchDestination = false;

	public PlaceView() {
	}

	@Override
	public boolean configure(final Element element,
			final ClientFactory clientFactory, Arguments args) {

		final String width = "100%";
		final String height = "100%";

		final EventBus switchBus = clientFactory.getEventBus("switch");

		this.remoteEventBus = clientFactory.getRemoteEventBus();

		EventBus eventBus = clientFactory.getEventBus(args.getArg(0));
		String options = args.getArg(1);

		boolean switchSource = options.contains("SwitchSource");
		switchDestination = options.contains("SwitchDestination");

		SwitchWidgetEvent.register(switchBus, new SwitchWidgetEventHandler(
				switchBus, element, this));

		if (eventBus != null) {
			SelectPtuEvent.subscribe(eventBus, new SelectPtuEvent.Handler() {

				@Override
				public void onPtuSelected(SelectPtuEvent event) {
					ptuId = event.getPtuId();

					remoteEventBus.fireEvent(new RequestRemoteEvent(
							PlaceChangedRemoteEvent.class));
				}
			});
		}

		PlaceChangedRemoteEvent.subscribe(remoteEventBus,
				new PlaceChangedRemoteEvent.Handler() {

					@Override
					public void onPlaceChanged(PlaceChangedRemoteEvent event) {
						if (ptuId == null)
							return;

						if (!ptuId.equals(event.getPtuId()))
							return;

						log.info("PLACE CHANGED " + event);
						Place place = event.getPlace();

/* MGWT
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
*/
						Image image = new Image(defaultImage);
						image.setWidth(width);
						setWidget(image);
						return;
					}
				});

		Image image = new Image(defaultImage);
		image.setWidth(width);
		setWidget(image);

		if (switchSource || switchDestination) {
			image.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					log.info("Click " + event + " switch");

					String title = element.getParentElement().getChild(1)
							.getChild(0).getNodeValue();

					log.info("Switch Widget: " + title);
					SwitchWidgetEvent.fire(switchBus, title, PlaceView.this,
							false);
				}
			});
		}

		return true;
	}

	@Override
	public boolean isDestination() {
		return switchDestination;
	}

	@Override
	public void toggleDestination() {
		switchDestination = !switchDestination;
	}
	
	@Override
	public boolean update() {
		return false;
	}
}
