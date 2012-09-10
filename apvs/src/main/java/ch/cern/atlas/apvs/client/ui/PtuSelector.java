package ch.cern.atlas.apvs.client.ui;

import java.util.Collections;
import java.util.List;

import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.tablet.LocalStorage;
import ch.cern.atlas.apvs.client.widget.OptionList;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.web.bindery.event.shared.EventBus;

public class PtuSelector extends VerticalFlowPanel {

	private ListBox list = new ListBox();

	private List<String> ptuIds;
	private String ptuId;

	public PtuSelector(final EventBus remoteEventBus,
			final EventBus localEventBus) {
		add(list);

		list.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				ptuId = null;
				try {
					ptuId = list.getItemText(list.getSelectedIndex());
				} catch (NumberFormatException e) {
					ptuId = null;
				}
				LocalStorage.getInstance().put(LocalStorage.PTU_ID, ptuId);
				localEventBus.fireEvent(new SelectPtuEvent(ptuId));
			}
		});

		RequestEvent.register(localEventBus, new RequestEvent.Handler() {

			@Override
			public void onRequestEvent(RequestEvent event) {
				if (event.getRequestedClassName().equals(
						SelectPtuEvent.class.getName())) {
					localEventBus.fireEvent(new SelectPtuEvent(ptuId));
				}
			}
		});

		PtuIdsChangedEvent.subscribe(remoteEventBus,
				new PtuIdsChangedEvent.Handler() {

					@Override
					public void onPtuIdsChanged(PtuIdsChangedEvent event) {

						ptuIds = event.getPtuIds();

						ptuId = LocalStorage.getInstance().get(
								LocalStorage.PTU_ID);
						((RemoteEventBus) localEventBus)
								.fireEvent(new SelectPtuEvent(ptuId));
						update();
						updateSelector();
					}
				});
		update();
	}

	private void updateSelector() {
		int i = 0;
		while (i < list.getItemCount()) {
			if (list.getValue(i).equals(ptuId)) {
				list.setSelectedIndex(i);
				break;
			}
			i++;
		}
		if (i == list.getItemCount()) {
			list.setSelectedIndex(0);
		}
		System.err.println("Update selector..." + i);

	}

	private void update() {
		if (ptuIds != null) {
			Collections.sort(ptuIds);
		}

		int selectedIndex = list.getSelectedIndex();
		String selectedItem = selectedIndex < 0 ? null : list
				.getItemText(selectedIndex);

		list.clear();
		List<String> items = new OptionList<String>(ptuIds, ptuIds.get(0));
		for (int index = 0; index < items.size(); index++) {
			String item = items.get(index);
			list.addItem(item);
			if ((selectedItem != null) && selectedItem.equals(item)) {
				list.setSelectedIndex(index);
			}
		}

		list.setEnabled(items.size() > 2);
	}

	public String getPtuId() {
		return ptuId;
	}
}
