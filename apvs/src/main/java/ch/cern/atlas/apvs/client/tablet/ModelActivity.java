package ch.cern.atlas.apvs.client.tablet;

import java.util.ArrayList;
import java.util.List;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.tablet.ModelEntrySelectedEvent.ModelEntry;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

public class ModelActivity extends MGWTAbstractActivity {

	private final ClientFactory clientFactory;

	private int oldIndex;

	private List<Item> items;

	public ModelActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;

	}

	@Override
	public void start(AcceptsOneWidget panel, final EventBus eventBus) {
		final ModelView view = clientFactory.getModelView();

		view.setBackButtonText("Home");
		view.setTitle("2D/3D Models");

		addHandlerRegistration(view.getBackButton().addTapHandler(
				new TapHandler() {

					@Override
					public void onTap(TapEvent event) {
						ActionEvent.fire(eventBus, ActionNames.BACK);

					}
				}));
		items = createItems();
		view.renderItems(items);

		addHandlerRegistration(view.getList().addCellSelectedHandler(
				new CellSelectedHandler() {

					@Override
					public void onCellSelected(CellSelectedEvent event) {
						int index = event.getIndex();

						view.setSelectedIndex(oldIndex, false);
						view.setSelectedIndex(index, true);
						oldIndex = index;

						ModelEntrySelectedEvent.fire(eventBus, items.get(index)
								.getEntry());

					}
				}));

		panel.setWidget(view);
	}

	private List<Item> createItems() {
		ArrayList<Item> list = new ArrayList<Item>();
		list.add(new Item("Run Layout", ModelEntry.RUN_LAYOUT));
		list.add(new Item("Tile Cal Barrel 3D", ModelEntry.TILE_CAL_BARREL_3D));
		list.add(new Item("Tile Cal Barrel DWG", ModelEntry.TILE_CAL_BARREL_DWG));
		return list;
	}

}
