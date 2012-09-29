package ch.cern.atlas.apvs.eventbus.client;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PollEventBus extends RemoteEventBus {

	private final Logger log = Logger.getLogger(getClass().getName());
	
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
		// send out locally
		super.fireEvent(event);

		// send out remote
		eventBusService.fireEvent(event, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				log.info("Client: Sent event..." + event);
			}

			@Override
			public void onFailure(Throwable caught) {
				log.warning("Failed to send event " + event + " "
						+ caught);
				caught.printStackTrace();
				if (caught.getCause() != null) {
					log.warning("Caused by...");
					caught.getCause().printStackTrace();
				}
			}
		});
	}

	private void getQueuedEvents() {
		eventBusService.getQueuedEvents(getUUID(),
				new AsyncCallback<List<RemoteEvent<?>>>() {

					@Override
					public void onSuccess(List<RemoteEvent<?>> events) {
					    log.info(getUUID()+": Received events..." + events.size());

						// forward events locally
						for (Iterator<RemoteEvent<?>> i = events.iterator(); i
								.hasNext();) {

							RemoteEvent<?> event = i.next();
//						    log.info("Client: Received event..." + event);
							// do not fire your own events
							if (event.getEventBusUUID() != getUUID()) {
								PollEventBus.super.fireEvent(event);
							}
						}

						getQueuedEvents();
					}

					@Override
					public void onFailure(Throwable caught) {
						log.warning("Failed to get next event " + caught);

						getQueuedEvents();
					}
				});
	}
}
