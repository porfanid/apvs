package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.AudioSupervisorSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.AudioUsersSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.MeetMeRemoteEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.service.AudioServiceAsync;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.client.settings.ConferenceRooms;
import ch.cern.atlas.apvs.client.settings.VoipAccount;
import ch.cern.atlas.apvs.client.widget.EditableCell;
import ch.cern.atlas.apvs.client.widget.GenericColumn;
import ch.cern.atlas.apvs.client.widget.GlassPanel;
import ch.cern.atlas.apvs.event.ConnectionStatusChangedRemoteEvent;
import ch.cern.atlas.apvs.event.ConnectionStatusChangedRemoteEvent.ConnectionType;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.EventBus;

public class AudioView extends GlassPanel implements Module {

	private CellTable<String> table = new CellTable<String>();
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private AudioSettings voipAccounts = new AudioSettings();
	private ConferenceRooms conferenceRooms = new ConferenceRooms();
	private String ptu = new String("PTUWeb");
	private VoipAccount supervisorAccount = new VoipAccount();

	private EventBus cmdBus;

	private List<String> fieldName = new ArrayList<String>(Arrays.asList(new String[] { "Status", "Private Call", "Group Call" }));
	private List<Class<?>> classField = new ArrayList<Class<?>>(Arrays.asList(new Class<?>[] { TextCell.class, ButtonCell.class,ButtonCell.class }));

	public AudioView() {
	}

	@Override
	public boolean configure(Element element, final ClientFactory clientFactory,
			Arguments args) {
		
		final RemoteEventBus eventBus = clientFactory.getRemoteEventBus();
		cmdBus = clientFactory.getEventBus(args.getArg(0));
		table.setWidth("100%");

		add(table, CENTER);
		
		// Status/Action Field column
		EditableCell fieldActionCell = new EditableCell(classField);
		
		GenericColumn<String> fieldActionCol = new GenericColumn<String>(fieldActionCell) {
			@Override
			public Object getValue(String fieldName) {
				if (fieldName.equals("Status"))
						return ((voipAccounts.getStatus(ptu)?"Online":"Offline"));
				else if (fieldName.equals("Private Call"))
					return ((voipAccounts.getOnCall(ptu) ? "Hangup '": "Call '") + voipAccounts.getUsername(ptu) + "'");
				else if (fieldName.equals("Group Call"))
					return ((!conferenceRooms.conferenceOfActivityExist(voipAccounts.getActivity(ptu)) ? ("Make "): (supervisorAccount.getOnConference() ? "Leave ": "Join "))+ "Impact conference");
				else if(fieldName.equals("Conference"))
					return "Close '" + voipAccounts.getActivity(ptu)+ "' conference";
				else if(fieldName.equals("Mute/Unmute"))
					return (voipAccounts.getMute(ptu) ? ("Unmute '") + voipAccounts.getUsername(ptu) + "'" : ("Mute '") + voipAccounts.getUsername(ptu) + "'");
				else if(fieldName.equals("Kick/Add"))
					return ( voipAccounts.getOnConference(ptu)? ( (voipAccounts.getRoom(ptu) == conferenceRooms.roomOfActivity(voipAccounts.getActivity(ptu))) ? ("Kick from '" + voipAccounts.getActivity(ptu) + "'") : ("Hangup from '" + voipAccounts.getActivity(ptu) + "' conference")) : ("Add to '" + voipAccounts.getActivity(ptu) + "'"));
				else
					return null;
			}
			@Override
			public void render(Context context, String object,
					SafeHtmlBuilder sb) {
				String value = (String) getValue(object);
				
				sb.append(SafeHtmlUtils.fromSafeConstant("<div class=\""
						+ value.toLowerCase() + "\">"));
				getCell().render(context, value, sb);
				sb.append(SafeHtmlUtils.fromSafeConstant("</div>"));
			}
			
		};
		
		fieldActionCol.setHorizontalAlignment(ALIGN_CENTER);
		fieldActionCol.setEnabled(clientFactory.isSupervisor());
		table.addColumn(fieldActionCol);
		
		fieldActionCol.setFieldUpdater(new FieldUpdater<String, Object>() {

			@Override
			public void update(int index, String fieldName, Object value) {
				if (!clientFactory.isSupervisor()) {
					return;
				}

				AsyncCallback<Void> callbackConference = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						System.err.println("FAIL CONFERENCE ESTABLISHMENT: "
								+ caught);
					}

					@Override
					public void onSuccess(Void result) {
						System.out.println("Conference established...");
					}
				};

				AsyncCallback<Void> callbackCall = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						System.err
								.println("FAIL CALL ESTABLISHMENT: " + caught);
					}

					@Override
					public void onSuccess(Void result) {
						System.out.println("Call Established...");
					}
				};

				AsyncCallback<Void> callbackHangup = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						System.err.println("FAIL HANGUP ESTABLISHMENT: " + caught);
					}

					@Override
					public void onSuccess(Void result) {
						System.out.println("Call Hangup...");
					}
				};
				
				AsyncCallback<Void> callbackMute = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						System.err.println("FAIL MUTE: " + caught);
					}

					@Override
					public void onSuccess(Void result) {
						System.out.println("Mute/Unmute Success...");
					}	
				};

				if (fieldName.equals("Private Call")) {
					if (!voipAccounts.getOnCall(ptu)) {
						List<String> channels = new ArrayList<String>();
						channels.add(supervisorAccount.getChannel());
						channels.add(voipAccounts.getChannel(ptu));
						
						// Hangup Supervisor and PTU User from active calls
						AudioServiceAsync.Util.getInstance().hangupMultiple(channels, callbackHangup);
						AudioServiceAsync.Util.getInstance().call(voipAccounts.getNumber(ptu),supervisorAccount.getNumber(), callbackCall);

					} else {
							AudioServiceAsync.Util.getInstance().hangup(supervisorAccount.getChannel(),callbackHangup);
							if (conferenceRooms.conferenceOfActivityExist(voipAccounts.getActivity(ptu))) {
								AudioServiceAsync.Util.getInstance().addToConference(voipAccounts.getNumber(ptu),conferenceRooms.roomOfActivity(voipAccounts.getActivity(ptu)),callbackConference);
						}
					}
					
				} else if (fieldName.equals("Group Call")){
					if (!supervisorAccount.getOnConference()) {
						if (!conferenceRooms.conferenceOfActivityExist(voipAccounts.getActivity(ptu))) {
							// Hangup Impact Activity Users from active calls
							AudioServiceAsync.Util.getInstance().hangupMultiple(voipAccounts.getActiveChannelsActivity(voipAccounts.getActivity(ptu)),callbackHangup);
							AudioServiceAsync.Util.getInstance().newConference(voipAccounts.getNumbersActivity(voipAccounts.getActivity(ptu)),callbackConference);
						} else{
							// Hangup Supervisor from active calls
							AudioServiceAsync.Util.getInstance().hangup(supervisorAccount.getChannel(),callbackHangup);
							AudioServiceAsync.Util.getInstance().addToConference(supervisorAccount.getAccount(),voipAccounts.getRoom(ptu),callbackConference);
						}
					} else {
						AudioServiceAsync.Util.getInstance().hangup(supervisorAccount.getChannel(),callbackHangup);
					}
					
				} else if (fieldName.equals("Conference")){
					AudioServiceAsync.Util.getInstance().hangupMultiple(voipAccounts.getActiveChannelsActivity(voipAccounts.getActivity(ptu)),callbackHangup);
					if( voipAccounts.getRoom(ptu).equals(supervisorAccount.getRoom()))
						AudioServiceAsync.Util.getInstance().hangup(supervisorAccount.getChannel(),callbackHangup);
					
				} else if (fieldName.equals("Mute/Unmute")){
					//System.out.println(voipAccounts.getMute(ptuId));
					if(voipAccounts.getMute(ptu)){
						AudioServiceAsync.Util.getInstance().unMuteUser(voipAccounts.getRoom(ptu),voipAccounts.getChannel(ptu), ptu, callbackMute);
					}else{
						AudioServiceAsync.Util.getInstance().muteUser(voipAccounts.getRoom(ptu),voipAccounts.getChannel(ptu), ptu, callbackMute);
					}
					
				} else if (fieldName.equals("Kick/Add")){
					if(voipAccounts.getOnConference(ptu))
						AudioServiceAsync.Util.getInstance().hangup(voipAccounts.getChannel(ptu),callbackHangup);
					else
						AudioServiceAsync.Util.getInstance().addToConference(voipAccounts.getNumber(ptu),conferenceRooms.roomOfActivity(voipAccounts.getActivity(ptu)),callbackConference);
					
				} else
					return;
			}
		});

		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<String>());

		if (cmdBus != null) {
			SelectPtuEvent.subscribe(cmdBus, new SelectPtuEvent.Handler() {

				@Override
				public void onPtuSelected(SelectPtuEvent event) {
					ptu = event.getPtu() != null ? event.getPtu().getName() : null;
					table.redraw();
				}
			});
		}

		ConnectionStatusChangedRemoteEvent.subscribe(eventBus,
				new ConnectionStatusChangedRemoteEvent.Handler() {

					@Override
					public void onConnectionStatusChanged(
							ConnectionStatusChangedRemoteEvent event) {
						if (event.getConnection() == ConnectionType.audio) {
							showGlass(!event.getStatus().isTrue());
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
						dataProvider.getList().addAll(fieldName);
					}
				});

		AudioSupervisorSettingsChangedRemoteEvent.subscribe(eventBus, new AudioSupervisorSettingsChangedRemoteEvent.Handler() {
			
			@Override
			public void onAudioSupervisorSettingsChanged(
					AudioSupervisorSettingsChangedRemoteEvent event) {
						supervisorAccount = event.getSupervisorSettings();
			}
		});
		
		MeetMeRemoteEvent.subscribe(eventBus, new MeetMeRemoteEvent.Handler() {

			@Override
			public void onMeetMeEvent(MeetMeRemoteEvent event) {
				conferenceRooms = event.getConferenceRooms();
				if (fieldName.size() > 3) {
					while (fieldName.size() > 3) {
						fieldName.remove(3);
						classField.remove(3);
					}
				}
				
				if (conferenceRooms.conferenceOfActivityExist(voipAccounts.getActivity(ptu))) {
					fieldName.add("Conference");
					classField.add(ButtonCell.class);
					fieldName.add("Mute/Unmute");
					classField.add(ButtonCell.class);
					fieldName.add("Kick/Add");
					classField.add(ButtonCell.class);
				}
				dataProvider.getList().clear();
				dataProvider.getList().addAll(fieldName);
			}
		});
		
		return true;
	}

	@Override
	public boolean update() {
		return false;
	}

}
