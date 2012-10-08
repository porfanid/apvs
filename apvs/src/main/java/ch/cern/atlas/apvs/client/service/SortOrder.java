package ch.cern.atlas.apvs.client.service;

import java.io.Serializable;

public class SortOrder implements Serializable {

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
