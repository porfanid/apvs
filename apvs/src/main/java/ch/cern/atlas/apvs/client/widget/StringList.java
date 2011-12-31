package ch.cern.atlas.apvs.client.widget;

import java.util.AbstractList;
import java.util.List;

public class StringList<T> extends AbstractList<String> {

	private List<T> list;
	
	public StringList(List<T> list) {
		this.list = list;
	}
	
	@Override
	public String get(int index) {
		return list != null ? list.get(index).toString() : null;
	}

	@Override
	public int size() {
		return list != null ? list.size() : 0;
	}
}
