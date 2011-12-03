package ch.cern.atlas.apvs.client;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class PtuSelector extends SimplePanel {

	private ListBox list = new ListBox();

	private List<Integer> ptuIds;

	public PtuSelector(final RemoteEventBus eventBus) {
		add(list);

		list.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int ptuId;
				try {
					ptuId = Integer.parseInt(list.getItemText(list
							.getSelectedIndex()));
				} catch (NumberFormatException e) {
					ptuId = -1;
				}
				eventBus.fireEvent(new SelectPtuEvent(ptuId));
			}
		});

		SelectPtuEvent.register(eventBus, new SelectPtuEvent.Handler() {

			@Override
			public void onPtuSelected(SelectPtuEvent event) {
				if (eventBus.getUUID() != event.getEventBusUUID())
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

		PtuIdsChangedEvent.subscribe(eventBus,
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
		list.clear();

		if (ptuIds != null) {
			list.addItem("Select PTU ID");
			Collections.sort(ptuIds);
			for (Iterator<Integer> i = ptuIds.iterator(); i.hasNext();) {
				list.addItem(toLabel(i.next()));
			}
			list.setEnabled(true);
		} else {
			list.addItem("...");
			list.setEnabled(false);
		}
	}

	private String toLabel(int ptuId) {
		return Integer.toString(ptuId);
	}
}
