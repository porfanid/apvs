package ch.cern.atlas.apvs.client.widget;

import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

// FIXME see http://code.google.com/p/google-web-toolkit/issues/detail?id=6865
public class ScrolledDataGrid<T> extends DataGrid<T> {
	public ScrollPanel getScrollPanel() {
		return (ScrollPanel) ((HeaderPanel) getWidget()).getContentWidget();
	}
}
