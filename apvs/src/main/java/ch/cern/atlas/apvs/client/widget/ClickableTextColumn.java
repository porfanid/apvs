package ch.cern.atlas.apvs.client.widget;

public abstract class ClickableTextColumn<T> extends ActiveColumn<T, String> {

	public ClickableTextColumn() {
		super(new ActiveClickableTextCell());
	}

	public ClickableTextColumn(ActiveCell<String> cell) {
		super(cell);
	}	
}
