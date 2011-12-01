package ch.cern.atlas.apvs.eventbus.shared;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEvent.Type;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.HandlerRegistration;

public interface RemoteEventBus {

	public <H> HandlerRegistration addHandler(Type<H> type, H handler);

	public <H> HandlerRegistration addHandlerToSource(Type<H> type,
			int uuid, H handler);

	public void fireEvent(Event<?> event);

	public void fireEventFromSource(Event<?> event, Object source);

	public void fireEvent(RemoteEvent<?> event);

	public void fireEventFromSource(RemoteEvent<?> event, int uuid);
}
