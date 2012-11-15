package ch.cern.atlas.apvs.client.tablet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.PlaceChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.places.SharedPlace;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.web.bindery.event.shared.EventBus;

public class TabletPanelActivityMapper implements ActivityMapper {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private final ClientFactory clientFactory;
	private RemoteEventBus remoteEventBus;
	private EventBus eventBus;

	private Place lastPlace;
	private String ptuId;

	public TabletPanelActivityMapper(final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		this.remoteEventBus = clientFactory.getRemoteEventBus();
		this.eventBus = clientFactory.getEventBus("local");

		SelectPtuEvent.subscribe(eventBus, new SelectPtuEvent.Handler() {

			@Override
			public void onPtuSelected(SelectPtuEvent event) {
				// FIXME needed ?
				// if
				// (!event.getEventBusUUID().equals(remoteEventBus.getUUID()))
				// return;

				log.info("PTU ID = " + event.getPtuId());
				ptuId = event.getPtuId();

				if (lastPlace instanceof SharedPlace) {
					remoteEventBus.fireEvent(new PlaceChangedRemoteEvent(ptuId,
							(SharedPlace) lastPlace));
				}
			}
		});

		RequestRemoteEvent.register(remoteEventBus,
				new RequestRemoteEvent.Handler() {

					@Override
					public void onRequestEvent(RequestRemoteEvent event) {
						if (event.getEventBusUUID().equals(
								remoteEventBus.getUUID()))
							return;

						if (event.getRequestedClassName().equals(
								PlaceChangedRemoteEvent.class.getName())) {
							if (lastPlace instanceof SharedPlace) {
								remoteEventBus
										.fireEvent(new PlaceChangedRemoteEvent(ptuId,
												(SharedPlace) lastPlace));
							}
						}
					}
				});
	}

	@Override
	public Activity getActivity(Place place) {
		if (place instanceof SharedPlace) {
			log.info("New REMOTE place " + place.getClass());
			remoteEventBus.fireEvent(
					new PlaceChangedRemoteEvent(ptuId, (SharedPlace) place));
		}
		Activity activity = getActivity(lastPlace, place);
		lastPlace = place;
		return activity;

	}

	private ImageActivity defaultActivity;

	private ImageActivity getDefaultActivity() {
		if (defaultActivity == null) {
			defaultActivity = new ImageActivity(clientFactory, "APVS",
					"Default-640x480.jpg");
		}

		return defaultActivity;
	}

	/* Main Panel */
	private Activity getActivity(Place lastPlace, Place newPlace) {
		if (newPlace instanceof HomePlace) {
			return getDefaultActivity();
		}

		if (newPlace instanceof ProcedureMenuPlace) {
			return getDefaultActivity();
		}

		if (newPlace instanceof CameraPlace) {
			CameraPlace place = (CameraPlace) newPlace;
			return new CameraActivity(clientFactory, place.getType());
		}

		if (newPlace instanceof ModelPlace) {
			return getDefaultActivity();
		}

		if (newPlace instanceof ProcedurePlace) {
			ProcedurePlace place = (ProcedurePlace) newPlace;
			return new ProcedureActivity(clientFactory, place.getUrl(),
					place.getName(), place.getStep());
		}

		if (newPlace instanceof ImagePlace) {
			ImagePlace place = (ImagePlace) newPlace;
			return new ImageActivity(clientFactory, place.getName(),
					place.getUrl());
		}

		log.warn("Tablet Activity not handled " + newPlace);

		return null;
	}
}
