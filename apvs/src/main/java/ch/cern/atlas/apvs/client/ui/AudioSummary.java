package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.List;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.AudioUsersSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.client.widget.GlassPanel;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Ternary;
import ch.cern.atlas.apvs.event.ConnectionStatusChangedRemoteEvent;
import ch.cern.atlas.apvs.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;

public class AudioSummary extends GlassPanel implements Module {

	private CellTable<String> table = new CellTable<String>();
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();

	private AudioSettings voipAccounts = new AudioSettings();
	protected Ternary audioOk = Ternary.Unknown;
	protected Ternary databaseConnect = Ternary.Unknown;

	public AudioSummary() {
	}

	@Override
	public boolean configure(Element element, final ClientFactory clientFactory,
			Arguments args) {
		final RemoteEventBus eventBus = clientFactory.getRemoteEventBus();
		
		table.setWidth("100%");
		
		add(table, CENTER);

		// PTU ID
		Column<String, String> ptuId = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return object;
			}
		};
		ptuId.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(ptuId, "PTU ID");

		// Username
		Column<String, String> username = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return voipAccounts.getUsername(object);
			}
		};
		username.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(username, "Username");

		// Status
		Column<String, String> status = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return (voipAccounts.getStatus(object)?"Online":"Offline");
			}
		};
		status.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(status, "Status");

		// Active Call
		Column<String, String> activeCall = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return voipAccounts.getDestUser(object);
			}
		};
		activeCall.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(activeCall, "Active Call");

		// Activity
		Column<String, String> activity = new Column<String, String>(
				new TextCell()) {
			@Override
			public String getValue(String object) {
				return voipAccounts.getActivity(object);
			}
		};
		activeCall.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(activity, "Activity");

		// Action
		Column<String, String> action = new Column<String, String>(
				new ButtonCell()) {
			@Override
			public String getValue(String object) {
				return (voipAccounts.getOnCall(object) ? "Hangup" : "Call");
			}
		};
		
		action.setFieldUpdater(new FieldUpdater<String, String>() {

			@Override
			public void update(int index, String object, String value) {				
				if (voipAccounts.getOnCall(object))
					
					clientFactory.getAudioService().hangup(voipAccounts.getChannel(object),new AsyncCallback<Void>() {

								@Override
								public void onSuccess(Void result) {
									System.err.println("Call Hangup...");
								}

								@Override
								public void onFailure(Throwable caught) {
									System.err
											.println("Fail to established the call "
													+ caught);
								}
							});
				else{
					clientFactory.getAudioService().call(voipAccounts.getNumber(object), "2000",new AsyncCallback<Void>() {

								@Override
								public void onSuccess(Void result) {
									System.err.println("Call Established...");
								}

								@Override
								public void onFailure(Throwable caught) {
									System.err
											.println("Fail to established the call "
													+ caught);
								}
							});
					}

			}
		});

		action.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(action);

		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<String>());

		ConnectionStatusChangedRemoteEvent.subscribe(eventBus,
				new ConnectionStatusChangedRemoteEvent.Handler() {

					@Override
					public void onConnectionStatusChanged(
							ConnectionStatusChangedRemoteEvent event) {
						switch (event.getConnection()) {
						case audio:
							audioOk = event.getStatus();
							break;
						case databaseConnect:
							databaseConnect = event.getStatus();
							break;
						default:
							break;
						}

						showGlass(audioOk.not().or(databaseConnect.not()).isTrue());
					}
				});

		// NOTE: FIXME the two below need to be merged...
		InterventionMapChangedRemoteEvent.subscribe(eventBus,
				new InterventionMapChangedRemoteEvent.Handler() {

					@Override
					public void onInterventionMapChanged(
							InterventionMapChangedRemoteEvent event) {
						List<Device> activePtus = event.getInterventionMap()
								.getPtus();
						dataProvider.getList().clear();
						for (Device device: activePtus) {
							dataProvider.getList().add(device.getName());
						}
					}
				});

		AudioUsersSettingsChangedRemoteEvent.subscribe(eventBus,
				new AudioUsersSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onAudioUsersSettingsChanged(
							AudioUsersSettingsChangedRemoteEvent event) {
						voipAccounts = event.getAudioSettings();

						dataProvider.getList().clear();
						dataProvider.getList().addAll(voipAccounts.getPtuIds());
					}
				});

		return true;

	}

	@Override
	public boolean update() {
		return false;
	}

}
