package ch.cern.atlas.apvs.eventbus.shared;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent.Type;

import com.google.web.bindery.event.shared.HandlerRegistration;

public interface RemoteEventBus {

	public <H> HandlerRegistration addHandler(Type<H> type, H handler);

	public <H> HandlerRegistration addHandlerToSource(Type<H> type,
			Object source, H handler);

	public void fireEvent(RemoteEvent<?> event);

	public void fireEventFromSource(RemoteEvent<?> event, Object source);
}
