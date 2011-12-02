package ch.cern.atlas.apvs.eventbus.client;

import java.util.Iterator;
import java.util.List;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.SimpleRemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PollEventBus extends SimpleRemoteEventBus {

	private EventBusServiceAsync eventBusService;

	public PollEventBus() {
		eventBusService = GWT.create(EventBusService.class);

		getQueuedEvents();
	}

	/**
	 * broadcast event
	 * 
	 */
	@Override
	public void fireEvent(RemoteEvent<?> event) {
		doFire(event);
	}

	/**
	 * broadcast event FIXME source is ignored
	 * 
	 */
	@Override
	public void fireEventFromSource(RemoteEvent<?> event, int uuid) {
		doFire(event);
	}

	private void doFire(final RemoteEvent<?> event) {
		setEventBusUuidOfEvent(event, getUUID());

		// send out locally
		super.fireEvent(event);

		// send out remote
		eventBusService.fireEvent(event, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				System.err.println("Client: Sent event..." + event);
			}

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Failed to send event " + event + " "
						+ caught);
			}
		});
	}

	private void getQueuedEvents() {
		eventBusService.getQueuedEvents(getUUID(),
				new AsyncCallback<List<RemoteEvent<?>>>() {

					@Override
					public void onSuccess(List<RemoteEvent<?>> events) {
						// System.err.println("Client: Received event..." +
						// event);

						// forward events locally
						for (Iterator<RemoteEvent<?>> i = events.iterator(); i
								.hasNext();) {

							RemoteEvent<?> event = i.next();
							// do not fire your own events
							if (event.getEventBusUUID() != getUUID()) {
								PollEventBus.super.fireEvent(event);
							}
						}

						getQueuedEvents();
					}

					@Override
					public void onFailure(Throwable caught) {
						System.err
								.println("Failed to get next event " + caught);

						getQueuedEvents();
					}
				});
	}
}
