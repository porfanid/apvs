package ch.cern.atlas.apvs.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

//NOTE: implements IsSerializable in case serialization file cannot be found
public class Directory extends DirectoryEntry implements Serializable, IsSerializable {

	private static final long serialVersionUID = -5848041198612728113L;
	protected List<DirectoryEntry> entries = new ArrayList<DirectoryEntry>();
	
	protected Directory() {
	}

	public Directory(Directory parent, String name) {
		super(parent, name);
	}
	
	public int getSize() {
		return entries.size();
	}
	
	public DirectoryEntry getEntry(int index) {
		return entries.get(index);
	}
}
