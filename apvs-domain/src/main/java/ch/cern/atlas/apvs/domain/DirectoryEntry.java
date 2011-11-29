package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

public class DirectoryEntry implements Serializable {

	private static final long serialVersionUID = -4603107928421487329L;
	private Directory parent;
	private String name;

	public DirectoryEntry() {
	}

	public DirectoryEntry(Directory parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	public Directory getParent() {
		return parent;
	}
	
	public String getName() {
		return name;
	}
}
