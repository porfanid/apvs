package ch.cern.atlas.apvs.eventbus.client;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.SimpleRemoteEventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PollEventBus extends SimpleRemoteEventBus {

	private EventBusServiceAsync eventBusService;

	public PollEventBus() {
		eventBusService = GWT.create(EventBusService.class);

		getNextEvent();
	}

	/**
	 * broadcast event and (receive it locally to distribute, below)
	 * 
	 */
	@Override
	public void fireEvent(RemoteEvent<?> event) {
		doFire(event);
	}

	/**
	 * broadcast event and (receive it locally to distribute, below) FIXME
	 * source is ignored
	 * 
	 */
	@Override
	public void fireEventFromSource(RemoteEvent<?> event, int uuid) {
		doFire(event);
	}

	private void doFire(final RemoteEvent<?> event) {
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

	private void getNextEvent() {
		eventBusService.getNextEvent(new AsyncCallback<RemoteEvent<?>>() {

			@Override
			public void onSuccess(RemoteEvent<?> event) {
//				System.err.println("Client: Received event..." + event);
				
				// forward event locally
				PollEventBus.super.fireEvent(event);
				
				getNextEvent();
			}

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Failed to get next event " + caught);
				
				getNextEvent();
			}
		});
	}
}
