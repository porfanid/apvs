package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.TableView.TableCell;

import org.jboss.netty.buffer.DynamicChannelBuffer;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.client.widget.ActiveCheckboxCell;
import ch.cern.atlas.apvs.client.widget.DynamicSelectionCell;
import ch.cern.atlas.apvs.client.widget.StringList;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

public class AsteriskSettingsView extends VerticalPanel implements Module{
	
	private CellTable<String> table = new CellTable<String>(); 
	private AudioSettings voipAccounts =  new AudioSettings();
	private List<String> activePtus = new ArrayList<String>();
	private List<String> listUsername = new ArrayList<String>();
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	
	public AsteriskSettingsView() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,Arguments args) {
		final RemoteEventBus eventBus = clientFactory.getRemoteEventBus();
		
		add(table);
		
		//ACTIVE
		//Column<String,Boolean> active = new Column<String,Boolean>(new ActiveCheckboxCell()) {
			
		//	public Boolean getValue(String object) {
		//		return activePtus.contains(object);
		//	}
		//};
		
		//table.addColumn(active, "Active");
		
		//PTU ID
		Column<String, String> ptuId = new Column<String, String>(new TextCell()) {

			@Override
			public String getValue(String object) {
				return object;
			}
		};
		ptuId.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(ptuId, "Ptu ID");
		
		//Username
		
		Column<String, String> username = new Column<String, String>(new DynamicSelectionCell(new StringList<String>(listUsername))){
			
			@Override
			public String getValue(String object) {
				return object;
			}
					
		};
		username.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(username, "Username");
		
		//Status
		
		Column<String, String> status = new Column<String, String>(new TextCell()) {
			
			@Override
			public String getValue(String object) {
				return object;
			}
		};		
		
		status.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(status, "Status");

		
		//Impact Activity
		Column<String, String> activity = new Column<String, String>(new EditTextCell()) {
			
			@Override
			public String getValue(String object) {
				return object;
			}
		};
		
		activity.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(activity, "Activity");

		
		
		List<String> a = new ArrayList<String>();
		a.add("Ola");
		
		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<String>());
		
		return true;
	}
	
	

}
