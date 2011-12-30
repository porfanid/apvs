package ch.cern.atlas.apvs.client;

import java.util.Collections;
import java.util.List;

import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class PtuSelector extends VerticalFlowPanel {

	private ListBox list = new ListBox();

	private List<Integer> ptuIds;
	private Integer ptuId;

	public PtuSelector(final RemoteEventBus remoteEventBus,
			final RemoteEventBus localEventBus) {
		add(list);

		list.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				ptuId = null;
				try {
					ptuId = Integer.parseInt(list.getItemText(list
							.getSelectedIndex()));
				} catch (NumberFormatException e) {
					ptuId = null;
				}
				localEventBus.fireEvent(new SelectPtuEvent(ptuId));
			}
		});

		RequestRemoteEvent.register(localEventBus,
				new RequestRemoteEvent.Handler() {

					@Override
					public void onRequestEvent(RequestRemoteEvent event) {
						if (event.getRequestedClassName().equals(
								SelectPtuEvent.class.getName())) {
							localEventBus.fireEvent(new SelectPtuEvent(ptuId));
						}
					}
				});

		SelectPtuEvent.register(localEventBus, new SelectPtuEvent.Handler() {

			@Override
			public void onPtuSelected(SelectPtuEvent event) {
				if (true) return;
				
				// NOTE: disabled, we do not have to set this...
				if (localEventBus.getUUID() != event.getEventBusUUID())
					return;

				int i = 0;
				while (i < list.getItemCount()) {
					if (list.getValue(i).equals(toLabel(event.getPtuId()))) {
						list.setSelectedIndex(i);
						break;
					}
					i++;
				}
				if (i == list.getItemCount()) {
					list.setSelectedIndex(0);
				}
			}
		});

		PtuIdsChangedEvent.subscribe(remoteEventBus,
				new PtuIdsChangedEvent.Handler() {

					@Override
					public void onPtuIdsChanged(PtuIdsChangedEvent event) {

						ptuIds = event.getPtuIds();
						update();
					}
				});
		update();
	}

	private void update() {
		if (ptuIds != null) {
			Collections.sort(ptuIds);
		}
		
		int selectedIndex = list.getSelectedIndex();
		String selectedItem = selectedIndex < 0 ? null : list.getItemText(selectedIndex);

		list.clear();
		List<String> items = new OptionList<Integer>(ptuIds, 0);
		for (int index = 0; index < items.size(); index++) {
			String item = items.get(index);
			list.addItem(item);
			if ((selectedItem != null) && selectedItem.equals(item)) {
				list.setSelectedIndex(index);
			}
		}

		list.setEnabled(items.size() > 2);
	}

	private String toLabel(Integer ptuId) {
		return ptuId != null ? Integer.toString(ptuId) : null;
	}

	public Integer getPtuId() {
		return ptuId;
	}
}
