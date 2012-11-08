package ch.cern.atlas.apvs.client.ui;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ch.cern.atlas.apvs.client.event.SelectDosimeterEvent;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterSerialNumbersChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class DosimeterSelector extends VerticalFlowPanel {

	private ListBox list = new ListBox();

	private List<String> serialNumbers;

	public DosimeterSelector(final RemoteEventBus eventBus) {
		add(list);

		list.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int serialNo;
				try {
					serialNo = Integer.parseInt(list.getItemText(list
							.getSelectedIndex()));
				} catch (NumberFormatException e) {
					serialNo = 0;
				}
				eventBus.fireEvent(new SelectDosimeterEvent(serialNo));
			}
		});

		SelectDosimeterEvent.register(eventBus,
				new SelectDosimeterEvent.Handler() {

					@Override
					public void onDosimeterSelected(SelectDosimeterEvent event) {
						if (eventBus.getUUID() != event.getEventBusUUID())
							return;

						int i = 0;
						while (i < list.getItemCount()) {
							if (list.getValue(i).equals(
									Integer.toString(event.getSerialNo()))) {
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

		DosimeterSerialNumbersChangedEvent.subscribe(eventBus,
				new DosimeterSerialNumbersChangedEvent.Handler() {

					@Override
					public void onDosimeterSerialNumbersChanged(
							DosimeterSerialNumbersChangedEvent event) {

						serialNumbers = event.getDosimeterSerialNumbers();
						update();
					}
				});
		update();
	}

	private void update() {
		list.clear();

		if (serialNumbers != null) {
			list.addItem("Select SerialNo");
			Collections.sort(serialNumbers);
			for (Iterator<String> i = serialNumbers.iterator(); i.hasNext();) {
				list.addItem(i.next());
			}
			list.setEnabled(true);
		} else {
			list.addItem("...");
			list.setEnabled(false);
		}
	}
}
