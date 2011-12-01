package ch.cern.atlas.apvs.eventbus.shared;

import java.io.Serializable;

public abstract class RemoteEvent<H> implements Serializable {

	private static final long serialVersionUID = -7670083062245182625L;

	public static class Type<H> {
		private static int nextHashCode;
		private final int index;

		public Type() {
			index = ++nextHashCode;
		}

		@Override
		public final int hashCode() {
			return index;
		}

		@Override
		public String toString() {
			return "RemoteEvent type of class "+getClass();
		}
	}

	private int uuid;

	protected RemoteEvent() {
	}

	public abstract Type<H> getAssociatedType();

	public int getUUID() {
		return uuid;
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

	protected void setUUID(int uuid) {
		this.uuid = uuid;
	}
}
