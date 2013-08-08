package ch.cern.atlas.apvs.client.service;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class SortOrder implements Serializable, IsSerializable {

	private static final long serialVersionUID = -894694876858263458L;

	private String name;
	private boolean ascending;
	
	public SortOrder() {
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
	
}
