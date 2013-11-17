package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class SortOrder implements Serializable, IsSerializable {

	private static final long serialVersionUID = -894694876858263458L;

	private String name;
	private boolean ascending;
	private boolean nullsFirst;
	
	protected SortOrder() {
	}

	public SortOrder(String name) {
		this(name, true);
	}
	
	public SortOrder(String name, boolean ascending) {
		this(name, ascending, true);
	}
	
	public SortOrder(String name, boolean ascending, boolean nullsFirst) {
		this.name = name;
		this.ascending = ascending;
		this.nullsFirst = nullsFirst;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isAscending() {
		return ascending;
	}
	
	public boolean isNullsFirst() {
		return nullsFirst;
	}

	@Override
	public String toString() {
		return "SortOrder [name=" + name + ", ascending=" + ascending
				+ ", nullsFirst=" + nullsFirst + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ascending ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (nullsFirst ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SortOrder other = (SortOrder) obj;
		if (ascending != other.ascending) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (nullsFirst != other.nullsFirst) {
			return false;
		}
		return true;
	}
	
	
	
}
