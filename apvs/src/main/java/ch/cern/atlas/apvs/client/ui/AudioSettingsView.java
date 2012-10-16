package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.TableView.TableCell;

import org.jboss.netty.buffer.DynamicChannelBuffer;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.AsteriskStatusEvent;
import ch.cern.atlas.apvs.client.event.AudioSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedEvent;
import ch.cern.atlas.apvs.client.service.AudioServiceAsync;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.client.widget.ActiveCheckboxCell;
import ch.cern.atlas.apvs.client.widget.DynamicSelectionCell;
import ch.cern.atlas.apvs.client.widget.StringList;
import ch.cern.atlas.apvs.client.widget.TextInputSizeCell;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

public class AudioSettingsView extends VerticalPanel implements Module{
	
	private CellTable<String> table = new CellTable<String>(); 
	private AudioSettings voipAccounts =  new AudioSettings();
	private List<String> activePtuIds = new ArrayList<String>();
	private List<String> usersList = new ArrayList<String>();
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	//private PtuSettings settings = new PtuSettings();

	
	public AudioSettingsView() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,Arguments args) {
		final RemoteEventBus eventBus = clientFactory.getRemoteEventBus();
		
		//TODO - Change the place of RPC SIP Peers List 
		//Call List Users
		AudioServiceAsync.Util.getInstance().usersList(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				System.err.println("Asterisk SIP users listed...");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Fail to list Asterisk SIP users " + caught);				
			}
		});
		
		add(table);
		
		//ACTIVE
		Column<String,Boolean> active = new Column<String,Boolean>(new ActiveCheckboxCell()) {
			
			public Boolean getValue(String object) {
				return activePtuIds.contains(object);
			}
		};
		table.addColumn(active, "Active");
		
		//PTU ID
		Column<String, String> ptuId = new Column<String, String>(new TextCell()) {

			@Override
			public String getValue(String object) {
				return object;
			}
		};
		ptuId.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(ptuId, "Ptu ID");

		// NAME
		Column<String, String> name = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return voipAccounts.getUsername(object);
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(name, "Name");
		
		//Number
		Column<String, String> number = new Column<String, String>(new DynamicSelectionCell(new StringList<String>(usersList))){
			
			@Override
			public String getValue(String object) {
				return voipAccounts.getNumber(object);
			}
					
		};
		number.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(number, "Number");
				
		number.setFieldUpdater(new FieldUpdater<String, String>() {
			
			@Override
			public void update(int index, String object, String value) {
				voipAccounts.setNumber(object, value);
				eventBus.fireEvent(new AudioSettingsChangedEvent(voipAccounts));
			}
		});
		
		//Impact Activity
		Column<String, String> activity = new Column<String, String>(new TextInputSizeCell(40)) {
			
			@Override
			public String getValue(String object) {
				return voipAccounts.getActivity(object);
			}
		};
		activity.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(activity, "Impact Activity");
		
		activity.setFieldUpdater(new FieldUpdater<String, String>() {
			
			@Override
			public void update(int index, String object, String value) {
				voipAccounts.setActivity(object, value);
				eventBus.fireEvent(new AudioSettingsChangedEvent(voipAccounts));
			}
		});
		
		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<String>());
		
		//*****************************
		//Events Registration
		PtuIdsChangedEvent.subscribe(eventBus,new PtuIdsChangedEvent.Handler() {

			@Override
			public void onPtuIdsChanged(PtuIdsChangedEvent event) {
				System.err.println("PTU IDS changed");
				activePtuIds = event.getPtuIds();
			}
		});
		
		AudioSettingsChangedEvent.subscribe(eventBus,new AudioSettingsChangedEvent.Handler() {
			
			@Override
			public void onAudioSettingsChanged(AudioSettingsChangedEvent event) {
				System.out.println("Audio Settings changed");
				voipAccounts = event.getAudioSettings();

				dataProvider.getList().clear();
				dataProvider.getList().addAll(voipAccounts.getPtuIds());
			}
		}); 		
		
		AsteriskStatusEvent.register(eventBus, new AsteriskStatusEvent.Handler() {
			
			@Override
			public void onAsteriskStatusChange(AsteriskStatusEvent event) {
				System.out.println("Asterisk Event");
				usersList.clear();
				usersList.addAll(event.getAsteriskUsersList());
			}
		});	
		
		return true;
	}
	
	

}
