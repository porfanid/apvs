package ch.cern.atlas.apvs.client.widget;


public abstract class DynamicSelectionColumn<T> extends ActiveColumn<T, String> {
	
	public DynamicSelectionColumn() {
		super(new ActiveDynamicSelectionCell());
	}
		
	public DynamicSelectionColumn(ActiveDynamicSelectionCell cell) {
		super(cell);
	}	
}
