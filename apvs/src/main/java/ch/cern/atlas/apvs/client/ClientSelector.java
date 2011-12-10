package ch.cern.atlas.apvs.client;

import java.util.Collections;
import java.util.List;

import ch.cern.atlas.apvs.client.event.SelectClientEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBusIdsChangedEvent;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class ClientSelector extends SimplePanel {

	private ListBox list = new ListBox();

	private List<Long> clientIds;
	private long selectedClientId = -1;

	public ClientSelector(final RemoteEventBus eventBus) {
		add(list);

		list.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				try {
					selectedClientId = Long.parseLong(list.getItemText(list
							.getSelectedIndex()));
				} catch (NumberFormatException e) {
					selectedClientId = -1;
				}
				eventBus.fireEvent(new SelectClientEvent(selectedClientId));
			}
		});
		
		RemoteEventBusIdsChangedEvent.subscribe(eventBus,
				new RemoteEventBusIdsChangedEvent.Handler() {

					@Override
					public void onClientIdsChanged(RemoteEventBusIdsChangedEvent event) {

						clientIds = event.getClientIds();
						update();
					}
				});

		update();
	}

	private void update() {
		list.clear();

		if (clientIds != null) {
			list.addItem("Select Client ID");
			Collections.sort(clientIds);
			int i = 0;
			while (i<clientIds.size()) {
				long clientId = clientIds.get(i);
				list.addItem(toLabel(clientId));
				i++;
				
				if (clientId == selectedClientId) {
					list.setSelectedIndex(i);
				}
			}
			list.setEnabled(true);
		} else {
			list.addItem("...");
			list.setEnabled(false);
		}
	}

	private String toLabel(long clientId) {
		return Long.toString(clientId);
	}

}
