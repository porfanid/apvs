package ch.cern.atlas.apvs.client.tablet;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

public class ProcedurePanel implements ProcedureUI {

	private LayoutPanel main;
	private HeaderPanel headerPanel;
	private HeaderButton headerBackButton;
	private CellList<ProcedureItem> cellListWithHeader;

	public ProcedurePanel() {
		main = new LayoutPanel();

//		main.getElement().setId("testdiv");

		headerPanel = new HeaderPanel();
		main.add(headerPanel);

		headerBackButton = new HeaderButton();
		headerBackButton.setBackButton(true);
		headerPanel.setLeftWidget(headerBackButton);
		headerBackButton.setVisible(!MGWT.getOsDetection().isAndroid());

		ScrollPanel scrollPanel = new ScrollPanel();

		cellListWithHeader = new CellList<ProcedureItem>(new BasicCell<ProcedureItem>() {

			@Override
			public String getDisplayString(ProcedureItem procedure) {
				return procedure.getDisplayString();
			}

			@Override
			public boolean canBeSelected(ProcedureItem procedure) {
				return true;
			}
		});
		cellListWithHeader.setRound(true);
		scrollPanel.setWidget(cellListWithHeader);
		scrollPanel.setScrollingEnabledX(false);

		main.add(scrollPanel);
	}

	@Override
	public Widget asWidget() {
		return main;
	}

	@Override
	public void setBackButtonText(String text) {
		headerBackButton.setText(text);

	}

	@Override
	public HasTapHandlers getBackButton() {
		return headerBackButton;
	}

	@Override
	public void setTitle(String title) {
		headerPanel.setCenter(title);

	}

	@Override
	public HasCellSelectedHandler getList() {
		return cellListWithHeader;
	}

	@Override
	public void renderItems(List<ProcedureItem> items) {
		cellListWithHeader.render(items);

	}

	@Override
	public void setSelectedIndex(int index, boolean selected) {
		cellListWithHeader.setSelectedIndex(index, selected);
	}
}
