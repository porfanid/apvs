package ch.cern.atlas.apvs.client.tablet;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ProcedureEntrySelectedEvent extends Event<ProcedureEntrySelectedEvent.Handler> {

	public enum ProcedureEntry {
		TILE_DRAWER_EXTRACTION, MURAL_PAINTING
	}

	public interface Handler {
		void onAnimationSelected(ProcedureEntrySelectedEvent event);
	}

	private static final Type<ProcedureEntrySelectedEvent.Handler> TYPE = new Type<ProcedureEntrySelectedEvent.Handler>();
	private final ProcedureEntry entry;

	public static void fire(EventBus eventBus, ProcedureEntry entry) {
		eventBus.fireEvent(new ProcedureEntrySelectedEvent(entry));
	}

	public static HandlerRegistration register(EventBus eventBus, Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	@Override
	public com.google.web.bindery.event.shared.Event.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	protected ProcedureEntrySelectedEvent(ProcedureEntry entry) {
		this.entry = entry;

	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onAnimationSelected(this);

	}

	public static Type<ProcedureEntrySelectedEvent.Handler> getType() {
		return TYPE;
	}

	public ProcedureEntry getEntry() {
		return entry;
	}
}
