package ch.cern.atlas.apvs.client;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ch.cern.atlas.apvs.client.event.SelectDosimeterEvent;
import ch.cern.atlas.apvs.client.service.DosimeterServiceAsync;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterSerialNumbersChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class DosimeterSelector extends SimplePanel {

	private ListBox list = new ListBox();

	private List<Integer> serialNumbers;

	public DosimeterSelector(final RemoteEventBus eventBus,
			DosimeterServiceAsync dosimeterService) {
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
									toLabel(event.getSerialNo()))) {
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

		dosimeterService.getSerialNumbers(new AsyncCallback<List<Integer>>() {

			private HandlerRegistration registration;

			@Override
			public void onSuccess(List<Integer> result) {
				// unregister any remaining handler
				if (registration != null) {
					registration.removeHandler();
					registration = null;
				}

				// set result
				serialNumbers = result;

				// register a new handler
				registration = DosimeterSerialNumbersChangedEvent.register(
						eventBus,
						new DosimeterSerialNumbersChangedEvent.Handler() {

							@Override
							public void onDosimeterSerialNumbersChanged(
									DosimeterSerialNumbersChangedEvent event) {

								serialNumbers = event
										.getDosimeterSerialNumbers();
								update();
							}
						});
				update();
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Could not retrieve serialNumbers");
			}
		});
	}

	private void update() {
		list.clear();

		if (serialNumbers != null) {
			list.addItem("Select SerialNo");
			Collections.sort(serialNumbers);
			for (Iterator<Integer> i = serialNumbers.iterator(); i.hasNext();) {
				list.addItem(Integer.toString(i.next()));
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
