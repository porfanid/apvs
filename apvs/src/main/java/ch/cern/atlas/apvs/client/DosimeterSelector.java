package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class DosimeterSelector extends SimplePanel implements HasChangeHandlers {
		
	private ListBox list = new ListBox();
	
	private DosimeterServiceAsync dosimeterService = GWT.create(DosimeterService.class);
	private List<Integer> serialNumbers = new ArrayList<Integer>();
 
	public DosimeterSelector() {
		add(list);
		getDosimeterSerialNumbers();		
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return list.addChangeHandler(handler);
	}
		
	private void getDosimeterSerialNumbers() {
		dosimeterService.getSerialNumbers(serialNumbers.hashCode(), new AsyncCallback<List<Integer>>() {
			
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
