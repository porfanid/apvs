package ch.cern.atlas.apvs.eventbus.shared;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Wraps an EventBus to hold on to any HandlerRegistrations, so that they can
 * easily all be cleared at once.
 */
public class ResettableRemoteEventBus extends RemoteEventBus {
	private final RemoteEventBus wrapped;
	private final Set<HandlerRegistration> registrations = new HashSet<HandlerRegistration>();

	public ResettableRemoteEventBus(RemoteEventBus wrappedBus) {
		this.wrapped = wrappedBus;
	}
	
	@Override
	public Long getUUID() {
		return wrapped.getUUID();
	}

	@Override
	public <H> HandlerRegistration addHandler(RemoteEvent.Type<H> type, H handler) {
		HandlerRegistration rtn = wrapped.addHandler(type, handler);
		return doRegisterHandler(rtn);
	}

	@Override
	public <H> HandlerRegistration addHandlerToSource(RemoteEvent.Type<H> type,
			int uuid, H handler) {
		HandlerRegistration rtn = wrapped.addHandlerToSource(type, uuid,
				handler);
		return doRegisterHandler(rtn);
	}

	@Override
	public void fireEvent(RemoteEvent<?> event) {
		wrapped.fireEvent(event);
	}

	@Override
	public void fireEventFromSource(RemoteEvent<?> event, int uuid) {
		wrapped.fireEventFromSource(event, uuid);
	}

	public void removeHandlers() {		
		Iterator<HandlerRegistration> it = registrations.iterator();
		while (it.hasNext()) {
			HandlerRegistration r = it.next();

			/*
			 * must remove before we call removeHandler. Might have come from
			 * nested ResettableRemoteEventBus
			 */
			it.remove();

			r.removeHandler();
		}
	}

	/**
	 * Visible for testing.
	 */
	protected int getRegistrationSize() {
		return registrations.size();
	}

	private HandlerRegistration doRegisterHandler(
			final HandlerRegistration registration) {
		registrations.add(registration);
		return new HandlerRegistration() {
			public void removeHandler() {
				doUnregisterHandler(registration);
			}
		};
	}

	private void doUnregisterHandler(HandlerRegistration registration) {
		if (registrations.contains(registration)) {
			registration.removeHandler();
			registrations.remove(registration);
		}
	}
}
