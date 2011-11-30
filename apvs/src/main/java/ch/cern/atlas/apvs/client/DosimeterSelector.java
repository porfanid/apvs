package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.cern.atlas.apvs.client.event.SelectDosimeterEvent;
import ch.cern.atlas.apvs.client.service.DosimeterServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

public class DosimeterSelector extends SimplePanel {
		
	private ListBox list = new ListBox();
	
	private DosimeterServiceAsync dosimeterService;
	private List<Integer> serialNumbers = new ArrayList<Integer>();
 
	public DosimeterSelector(final EventBus eventBus, DosimeterServiceAsync dosimeterService) {
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
	
}
