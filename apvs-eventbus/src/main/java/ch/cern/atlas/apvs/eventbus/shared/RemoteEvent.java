package ch.cern.atlas.apvs.eventbus.shared;

import java.io.Serializable;

public abstract class RemoteEvent<H> implements Serializable {

	private static final long serialVersionUID = -7670083062245182625L;
	
	public static class Type<H> {
		private final int uuid;

		public Type() {
			uuid = UUID.uuidInt(8);
		}
		
		public int getUUID() {
			return uuid;
		}

		@Override
		public final int hashCode() {
			return (int)uuid;
		}

		@Override
		public String toString() {
			return "RemoteEvent type of class "+getClass();
		}
	}
	
	private Integer uuid = null;
	private Long eventBusUUID = null;

	protected RemoteEvent() {
	}

	public abstract Type<H> getAssociatedType();

	public int getSourceUUID() {
		return uuid;
	}

	public long getEventBusUUID() {
		return eventBusUUID;
	}
	
	public String toDebugString() {
		String name = this.getClass().getName();
		name = name.substring(name.lastIndexOf(".") + 1);
		return "event: " + name + ":";
	}

	@Override
	public String toString() {
		return "Remote event of class "+getClass();
	}

	protected abstract void dispatch(H handler);

	protected void setSourceUUID(int uuid) {
		// only set when it was not set before. Forwarded events keep their original source.
		if (this.uuid == null) {
			this.uuid = uuid;
		}
	}

	protected void setEventBusUUID(long eventBusUUID) {
		// only set when it was not set before. Forwarded events do not get their UUID re-set.
		if (this.eventBusUUID == null) {
			this.eventBusUUID = eventBusUUID;
		}
	}
}
