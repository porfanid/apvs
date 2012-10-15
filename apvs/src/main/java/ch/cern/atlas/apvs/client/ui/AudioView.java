package ch.cern.atlas.apvs.client.ui;

import java.awt.Checkbox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.AudioSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedEvent;
import ch.cern.atlas.apvs.client.service.AudioServiceAsync;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.client.widget.ActiveCheckboxCell;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.dom.client.Element;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;



public class AudioView extends VerticalPanel implements Module{


	private CellTable<String> table = new CellTable<String>();
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	
	private List<String> activePtuIds = new ArrayList<String>();
	private AudioSettings voipAccounts = new AudioSettings();
	
	public AudioView() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory, Arguments args) {
		final RemoteEventBus eventBus = clientFactory.getRemoteEventBus();
		
		add(table);
		
		//PTU ID
		Column<String, String> ptuId = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return object;
			}			
		};
		ptuId.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		//table.addColumn(ptuId, "PTU ID");
		
		//Username
		Column<String, String> username = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return voipAccounts.getUsername(object);
			}			
		};
		username.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(username, "Username");
				
		//Status
		Column<String, String> status = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return voipAccounts.getStatus(object);
			}			
		};
		status.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(status, "Status");
		
		
		//Active Call
		Column<String, String> activeCall = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return voipAccounts.getDestUser(object);
			}			
		};
		activeCall.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(activeCall, "Active Call");
		
		//Activity
		Column<String, String> activity = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return voipAccounts.getActivity(object);
			}			
		};
		activeCall.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(activity, "Activity");
		
		
		//Action
		Column<String, String> action = new Column<String, String>(new ButtonCell()) {
			@Override
			public String getValue(String object) {
				return (voipAccounts.getOnCall(object) ? "Hangup" : "Call");
			}		
		};
		action.setFieldUpdater(new FieldUpdater<String, String>() {
			
			@Override
			public void update(int index, String object, String value) {
				// TODO Implementation of the button Handler
				if(voipAccounts.getOnCall(object))
					AudioServiceAsync.Util.getInstance().hangup(voipAccounts.getChannel(object), new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							System.err.println("Call Hangup...");	
						}
						
						@Override
						public void onFailure(Throwable caught) {
							System.err.println("Fail to established the call " + caught);		
						}
					});
				else
					AudioServiceAsync.Util.getInstance().call(voipAccounts.getNumber(object),"2000", new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						System.err.println("Call Established...");
					}
					
					@Override
					public void onFailure(Throwable caught) {
						System.err.println("Fail to established the call " + caught);				
					}
				});
			}
		});
		
		action.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(action);
		
		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<String>());

		PtuIdsChangedEvent.subscribe(eventBus,new PtuIdsChangedEvent.Handler() {

			@Override
			public void onPtuIdsChanged(PtuIdsChangedEvent event) {
				activePtuIds = event.getPtuIds();
				dataProvider.getList().clear();
				dataProvider.getList().addAll(activePtuIds);
			}
		});
		 	
		AudioSettingsChangedEvent.subscribe(eventBus, new AudioSettingsChangedEvent.Handler() {
			
			@Override
			public void onAudioSettingsChanged(AudioSettingsChangedEvent event) {
				voipAccounts = event.getAudioSettings();
				
				dataProvider.getList().clear();
				dataProvider.getList().addAll(voipAccounts.getPtuIds());
			}
		});
		
		
		return true;

	}
		
}
	
