package ch.cern.atlas.apvs.eventbus.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.event.shared.UmbrellaException;

/**
 * EventBus that handles normal Events (local) and RemoteEvents
 * 
 * @author Mark Donszelmann
 */
public class SimpleRemoteEventBus extends SimpleEventBus implements RemoteEventBus {

	protected static <H> void dispatchEvent(RemoteEvent<H> event, H handler) {
		event.dispatch(handler);
	}

//	protected static void setSourceOfEvent(RemoteEvent<?> event, Object source) {
//		event.setSource(source);
//	}

	
	private interface Command {
		void execute();
	}

	private int firingDepth = 0;

	private List<Command> deferredDeltas;

	private final Map<RemoteEvent.Type<?>, Map<Object, List<?>>> map = new HashMap<RemoteEvent.Type<?>, Map<Object, List<?>>>();

	public SimpleRemoteEventBus() {
	}

	@Override
	public <H> HandlerRegistration addHandler(RemoteEvent.Type<H> type, H handler) {
		return doAdd(type, null, handler);
	}

	@Override
	public <H> HandlerRegistration addHandlerToSource(final RemoteEvent.Type<H> type,
			final Object source, final H handler) {
		if (source == null) {
			throw new NullPointerException(
					"Cannot add a handler with a null source");
		}

		return doAdd(type, source, handler);
	}

	@Override
	public void fireEvent(RemoteEvent<?> event) {
		doFire(event, null);
	}

	@Override
	public void fireEventFromSource(RemoteEvent<?> event, Object source) {
		if (source == null) {
			throw new NullPointerException("Cannot fire from a null source");
		}
		doFire(event, source);
	}

	private void defer(Command command) {
		if (deferredDeltas == null) {
			deferredDeltas = new ArrayList<Command>();
		}
		deferredDeltas.add(command);
	}

	private <H> HandlerRegistration doAdd(final RemoteEvent.Type<H> type,
			final Object source, final H handler) {
		if (type == null) {
			throw new NullPointerException(
					"Cannot add a handler with a null type");
		}
		if (handler == null) {
			throw new NullPointerException("Cannot add a null handler");
		}

		if (firingDepth > 0) {
			enqueueAdd(type, source, handler);
		} else {
			doAddNow(type, source, handler);
		}

		return new HandlerRegistration() {
			public void removeHandler() {
				if (firingDepth > 0) {
					enqueueRemove(type, source, handler);
				} else {
					doRemoveNow(type, source, handler);
				}
			}
		};
	}

	private <H> void doAddNow(RemoteEvent.Type<H> type, Object source, H handler) {
		List<H> l = ensureHandlerList(type, source);
		l.add(handler);
	}

	private <H> void doFire(RemoteEvent<H> event, Object source) {
		if (event == null) {
			throw new NullPointerException("Cannot fire null event");
		}
		try {
			firingDepth++;

			if (source != null) {
//				setSourceOfEvent(event, source);
			}

			List<H> handlers = getDispatchList(event.getAssociatedType(),
					source);
			Set<Throwable> causes = null;

			ListIterator<H> it = handlers.listIterator();
			while (it.hasNext()) {
				H handler = it.next();

				try {
					dispatchEvent(event, handler);
				} catch (Throwable e) {
					if (causes == null) {
						causes = new HashSet<Throwable>();
					}
					causes.add(e);
				}
			}

			if (causes != null) {
				throw new UmbrellaException(causes);
			}
		} finally {
			firingDepth--;
			if (firingDepth == 0) {
				handleQueuedAddsAndRemoves();
			}
		}
	}

	private <H> void doRemoveNow(RemoteEvent.Type<H> type, Object source, H handler) {
		List<H> l = getHandlerList(type, source);

		boolean removed = l.remove(handler);
		assert removed : "redundant remove call";
		if (removed && l.isEmpty()) {
			prune(type, source);
		}
	}

	private <H> void enqueueAdd(final RemoteEvent.Type<H> type, final Object source,
			final H handler) {
		defer(new Command() {
			public void execute() {
				doAddNow(type, source, handler);
			}
		});
	}

	private <H> void enqueueRemove(final RemoteEvent.Type<H> type,
			final Object source, final H handler) {
		defer(new Command() {
			public void execute() {
				doRemoveNow(type, source, handler);
			}
		});
	}

	private <H> List<H> ensureHandlerList(RemoteEvent.Type<H> type, Object source) {
		Map<Object, List<?>> sourceMap = map.get(type);
		if (sourceMap == null) {
			sourceMap = new HashMap<Object, List<?>>();
			map.put(type, sourceMap);
		}

		// safe, we control the puts.
		@SuppressWarnings("unchecked")
		List<H> handlers = (List<H>) sourceMap.get(source);
		if (handlers == null) {
			handlers = new ArrayList<H>();
			sourceMap.put(source, handlers);
		}

		return handlers;
	}

	private <H> List<H> getDispatchList(RemoteEvent.Type<H> type, Object source) {
		List<H> directHandlers = getHandlerList(type, source);
		if (source == null) {
			return directHandlers;
		}

		List<H> globalHandlers = getHandlerList(type, null);

		List<H> rtn = new ArrayList<H>(directHandlers);
		rtn.addAll(globalHandlers);
		return rtn;
	}

	private <H> List<H> getHandlerList(RemoteEvent.Type<H> type, Object source) {
		Map<Object, List<?>> sourceMap = map.get(type);
		if (sourceMap == null) {
			return Collections.emptyList();
		}

		// safe, we control the puts.
		@SuppressWarnings("unchecked")
		List<H> handlers = (List<H>) sourceMap.get(source);
		if (handlers == null) {
			return Collections.emptyList();
		}

		return handlers;
	}

	private void handleQueuedAddsAndRemoves() {
		if (deferredDeltas != null) {
			try {
				for (Command c : deferredDeltas) {
					c.execute();
				}
			} finally {
				deferredDeltas = null;
			}
		}
	}

	private void prune(RemoteEvent.Type<?> type, Object source) {
		Map<Object, List<?>> sourceMap = map.get(type);

		List<?> pruned = sourceMap.remove(source);

		assert pruned != null : "Can't prune what wasn't there";
		assert pruned.isEmpty() : "Pruned unempty list!";

		if (sourceMap.isEmpty()) {
			map.remove(type);
		}
	}
}
