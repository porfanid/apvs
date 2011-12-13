package ch.cern.atlas.apvs.client.tablet;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ModelEntrySelectedEvent extends Event<ModelEntrySelectedEvent.Handler> {

	public enum ModelEntry {
		RUN_LAYOUT, TILE_CAL_BARREL_3D, TILE_CAL_BARREL_DWG
	}

	public interface Handler {
		void onModelEntrySelected(ModelEntrySelectedEvent event);
	}

	private static final Type<ModelEntrySelectedEvent.Handler> TYPE = new Type<ModelEntrySelectedEvent.Handler>();
	private final ModelEntry entry;

	public static void fire(EventBus eventBus, ModelEntry entry) {
		eventBus.fireEvent(new ModelEntrySelectedEvent(entry));
	}

	public static HandlerRegistration register(EventBus eventBus, Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	@Override
	public com.google.web.bindery.event.shared.Event.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	protected ModelEntrySelectedEvent(ModelEntry entry) {
		this.entry = entry;

	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onModelEntrySelected(this);

	}

	public static Type<ModelEntrySelectedEvent.Handler> getType() {
		return TYPE;
	}

	public ModelEntry getEntry() {
		return entry;
	}
}
