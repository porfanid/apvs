package ch.cern.atlas.apvs.client.tablet;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;


public interface ProcedureUI extends IsWidget {
	public void setBackButtonText(String text);

	public HasTapHandlers getBackButton();

	public void setTitle(String title);

	public HasCellSelectedHandler getList();

	public void renderItems(List<ProcedureItem> items);

	public void setSelectedIndex(int index, boolean selected);
}
