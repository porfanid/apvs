package ch.cern.atlas.apvs.client.ui;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class DummyEventBus extends EventBus {

	@Override
	public <H> HandlerRegistration addHandler(Type<H> type, H handler) {
		return null;
	}

	@Override
	public <H> HandlerRegistration addHandlerToSource(Type<H> type,
			Object source, H handler) {
		return addHandler(type, handler);
	}

	@Override
	public void fireEvent(Event<?> event) {
		// ignore
	}

	@Override
	public void fireEventFromSource(Event<?> event, Object source) {
		// ignore
	}

}
