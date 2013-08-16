package ch.cern.atlas.apvs.domain;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class DirectoryEntry implements Serializable, IsSerializable {

	private static final long serialVersionUID = -4603107928421487329L;
	private Directory parent;
	private String name;

	protected DirectoryEntry() {
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
