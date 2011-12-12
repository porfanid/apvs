package ch.cern.atlas.apvs.client;

import java.util.List;

public class OptionList<T> extends StringList<T> {

	private T selected;

	public OptionList(List<T> list, T selected) {
		super(list);

		this.selected = selected;
	}

	@Override
	public String get(int index) {
		switch (index) {
		case 0:
			return "Select ...";
		case 1:
			return selected.toString();
		default:
			return super.get(index - 2);
		}
	}

	@Override
	public int size() {
		return 2 + super.size();
	}
}
