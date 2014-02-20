package ch.cern.atlas.apvs.client.widget;


public abstract class CheckboxColumn<T> extends ActiveColumn<T, Boolean> {
	
	public CheckboxColumn() {
		super(new ActiveCheckboxCell());
	}
		
	public CheckboxColumn(ActiveCheckboxCell cell) {
		super(cell);
	}	
}
