package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.cern.atlas.apvs.client.event.SelectDosimeterEvent;
import ch.cern.atlas.apvs.client.service.DosimeterServiceAsync;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class DosimeterSelector extends SimplePanel {
		
	private ListBox list = new ListBox();
	
	private DosimeterServiceAsync dosimeterService;
	private List<Integer> serialNumbers = new ArrayList<Integer>();
 
	public DosimeterSelector(final RemoteEventBus eventBus, DosimeterServiceAsync dosimeterService) {
		this.dosimeterService = dosimeterService;
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
		
		SelectDosimeterEvent.register(eventBus, new SelectDosimeterEvent.Handler() {
			
			@Override
			public void onDosimeterSelected(SelectDosimeterEvent event) {
				if (eventBus.getUUID() != event.getEventBusUUID()) return;
				
				int i = 0;
				while (i < list.getItemCount()) {
					if (list.getValue(i).equals(toLabel(event.getSerialNo()))) {
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
		
		getDosimeterSerialNumbers();		
	}
		
	private void getDosimeterSerialNumbers() {
		dosimeterService.getSerialNumbers((long)serialNumbers.hashCode(), new AsyncCallback<List<Integer>>() {
			
			@Override
			public void onSuccess(List<Integer> result) {
				if (result == null) {
					System.err.println("FIXME onSuccess null in dosimeterSelector");
					return;
				}
				serialNumbers  = result;
				
				list.clear();
				list.addItem("Select SerialNo");
				for (Iterator<Integer> i=serialNumbers.iterator(); i.hasNext();) {
					list.addItem(Integer.toString(i.next()));
				}
				
				getDosimeterSerialNumbers();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Could not retrieve dosimeterSerialNumbers");
				
				getDosimeterSerialNumbers();
			}
		});

	}

	private String toLabel(int ptuId) {
		return Integer.toString(ptuId);
	}
}
