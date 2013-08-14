package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class SortOrder implements Serializable, IsSerializable {

	private static final long serialVersionUID = -894694876858263458L;

	private String name;
	private boolean ascending;
	
	public SortOrder() {
	}

	public SortOrder(String name) {
		this(name, true);
	}
	
	public SortOrder(String name, boolean ascending) {
		this.name = name;
		this.ascending = ascending;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isAscending() {
		return ascending;
	}

	@Override
	public String toString() {
		return "SortOrder [name=" + name + ", ascending=" + ascending + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ascending ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		return true;
	}
	
	
	
}
