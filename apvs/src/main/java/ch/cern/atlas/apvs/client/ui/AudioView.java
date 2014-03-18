package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.AudioSupervisorSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.AudioUsersSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.MeetMeRemoteEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.client.settings.ConferenceRooms;
import ch.cern.atlas.apvs.client.settings.VoipAccount;
import ch.cern.atlas.apvs.client.widget.ActiveColumn;
import ch.cern.atlas.apvs.client.widget.EditableCell;
import ch.cern.atlas.apvs.client.widget.GlassPanel;
import ch.cern.atlas.apvs.domain.Device;
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

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private CellTable<String> table = new CellTable<String>();
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private AudioSettings voipAccounts = new AudioSettings();
	private ConferenceRooms conferenceRooms = new ConferenceRooms();
	private Device ptu = null;
	private VoipAccount supervisorAccount = new VoipAccount();

	private EventBus cmdBus;

	private List<String> fieldName = new ArrayList<String>(
			Arrays.asList(new String[] { "Status", "Private Call", "Group Call" }));
	private List<Class<?>> classField = new ArrayList<Class<?>>(
			Arrays.asList(new Class<?>[] { TextCell.class, ButtonCell.class,
					ButtonCell.class }));

	public AudioView() {
	}

	@Override
	public boolean configure(Element element,
			final ClientFactory clientFactory, Arguments args) {

		final RemoteEventBus eventBus = clientFactory.getRemoteEventBus();
		cmdBus = clientFactory.getEventBus(args.getArg(0));
		table.setWidth("100%");

		add(table);

		// Status/Action Field column
		EditableCell fieldActionCell = new EditableCell(classField);

		ActiveColumn<String, Object> fieldActionCol = new ActiveColumn<String, Object>(
				fieldActionCell) {
			@Override
			public Object getValue(String fieldName) {
				if (ptu == null) {
					return null;
				}

				String ptuId = ptu.getName();
				if (fieldName.equals("Status"))
					return ((voipAccounts.getStatus(ptuId) ? "Online"
							: "Offline"));
				else if (fieldName.equals("Private Call"))
					return ((voipAccounts.getOnCall(ptuId) ? "Hangup '"
							: "Call '") + voipAccounts.getUsername(ptuId) + "'");
				else if (fieldName.equals("Group Call"))
					return ((!conferenceRooms
							.conferenceOfActivityExist(voipAccounts
									.getActivity(ptuId)) ? ("Make ")
							: (supervisorAccount.getOnConference() ? "Leave "
									: "Join ")) + "Impact conference");
				else if (fieldName.equals("Conference"))
					return "Close '" + voipAccounts.getActivity(ptuId)
							+ "' conference";
				else if (fieldName.equals("Mute/Unmute"))
					return (voipAccounts.getMute(ptuId) ? ("Unmute '")
							+ voipAccounts.getUsername(ptuId) + "'"
							: ("Mute '") + voipAccounts.getUsername(ptuId)
									+ "'");
				else if (fieldName.equals("Kick/Add"))
					return (voipAccounts.getOnConference(ptuId) ? ((voipAccounts
							.getRoom(ptuId) == conferenceRooms
							.roomOfActivity(voipAccounts.getActivity(ptuId))) ? ("Kick from '"
							+ voipAccounts.getActivity(ptuId) + "'")
							: ("Hangup from '"
									+ voipAccounts.getActivity(ptuId) + "' conference"))
							: ("Add to '" + voipAccounts.getActivity(ptuId) + "'"));
				else
					return null;
			}

			@Override
			public void render(Context context, String object,
					SafeHtmlBuilder sb) {
				String value = (String) getValue(object);

				if (value != null) {
					sb.append(SafeHtmlUtils.fromSafeConstant("<div class=\""
							+ value.toLowerCase() + "\">"));
					getCell().render(context, value, sb);
					sb.append(SafeHtmlUtils.fromSafeConstant("</div>"));
				}
			}

		};

		fieldActionCol.setHorizontalAlignment(ActiveColumn.ALIGN_CENTER);
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
						log.warn("FAIL CONFERENCE ESTABLISHMENT: "
								+ caught);
					}

					@Override
					public void onSuccess(Void result) {
						log.info("Conference established...");
					}
				};

				AsyncCallback<Void> callbackCall = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						log.warn("FAIL CALL ESTABLISHMENT: " + caught);
					}

					@Override
					public void onSuccess(Void result) {
						log.info("Call Established...");
					}
				};

				AsyncCallback<Void> callbackHangup = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						log.warn("FAIL HANGUP ESTABLISHMENT: "
								+ caught);
					}

					@Override
					public void onSuccess(Void result) {
						log.info("Call Hangup...");
					}
				};

				AsyncCallback<Void> callbackMute = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						log.warn("FAIL MUTE: " + caught);
					}

					@Override
					public void onSuccess(Void result) {
						log.info("Mute/Unmute Success...");
					}
				};

				String ptuId = ptu.getName();
				if (fieldName.equals("Private Call")) {
					if (!voipAccounts.getOnCall(ptuId)) {
						List<String> channels = new ArrayList<String>();
						channels.add(supervisorAccount.getChannel());
						channels.add(voipAccounts.getChannel(ptuId));

						// Hangup Supervisor and PTU User from active calls
						clientFactory.getAudioService().hangupMultiple(
								channels, callbackHangup);
						clientFactory.getAudioService().call(
								voipAccounts.getNumber(ptuId),
								supervisorAccount.getNumber(), callbackCall);

					} else {
						clientFactory.getAudioService().hangup(
								supervisorAccount.getChannel(), callbackHangup);
						if (conferenceRooms
								.conferenceOfActivityExist(voipAccounts
										.getActivity(ptuId))) {
							clientFactory.getAudioService().addToConference(
									voipAccounts.getNumber(ptuId),
									conferenceRooms.roomOfActivity(voipAccounts
											.getActivity(ptuId)),
									callbackConference);
						}
					}

				} else if (fieldName.equals("Group Call")) {
					if (!supervisorAccount.getOnConference()) {
						if (!conferenceRooms
								.conferenceOfActivityExist(voipAccounts
										.getActivity(ptuId))) {
							// Hangup Impact Activity Users from active calls
							clientFactory
									.getAudioService()
									.hangupMultiple(
											voipAccounts
													.getActiveChannelsActivity(voipAccounts
															.getActivity(ptuId)),
											callbackHangup);
							clientFactory.getAudioService().newConference(
									voipAccounts
											.getNumbersActivity(voipAccounts
													.getActivity(ptuId)),
									callbackConference);
						} else {
							// Hangup Supervisor from active calls
							clientFactory.getAudioService().hangup(
									supervisorAccount.getChannel(),
									callbackHangup);
							clientFactory.getAudioService().addToConference(
									supervisorAccount.getAccount(),
									voipAccounts.getRoom(ptuId),
									callbackConference);
						}
					} else {
						clientFactory.getAudioService().hangup(
								supervisorAccount.getChannel(), callbackHangup);
					}

				} else if (fieldName.equals("Conference")) {
					clientFactory.getAudioService().hangupMultiple(
							voipAccounts.getActiveChannelsActivity(voipAccounts
									.getActivity(ptuId)), callbackHangup);
					if (voipAccounts.getRoom(ptuId).equals(
							supervisorAccount.getRoom()))
						clientFactory.getAudioService().hangup(
								supervisorAccount.getChannel(), callbackHangup);

				} else if (fieldName.equals("Mute/Unmute")) {
					if (voipAccounts.getMute(ptuId)) {
						clientFactory.getAudioService().unMuteUser(
								voipAccounts.getRoom(ptuId),
								voipAccounts.getChannel(ptuId), ptuId,
								callbackMute);
					} else {
						clientFactory.getAudioService().muteUser(
								voipAccounts.getRoom(ptuId),
								voipAccounts.getChannel(ptuId), ptuId,
								callbackMute);
					}

				} else if (fieldName.equals("Kick/Add")) {
					if (voipAccounts.getOnConference(ptuId))
						clientFactory.getAudioService().hangup(
								voipAccounts.getChannel(ptuId), callbackHangup);
					else
						clientFactory.getAudioService().addToConference(
								voipAccounts.getNumber(ptuId),
								conferenceRooms.roomOfActivity(voipAccounts
										.getActivity(ptuId)),
								callbackConference);

				} else
					return;
			}
		});

		dataProvider.addDataDisplay(table);

		dataProvider.getList().clear();
		dataProvider.getList().addAll(fieldName);

		// dataProvider.setList(new ArrayList<String>());

		if (cmdBus != null) {
			SelectPtuEvent.subscribe(cmdBus, new SelectPtuEvent.Handler() {

				@Override
				public void onPtuSelected(SelectPtuEvent event) {
					ptu = event.getPtu();
					updateTable();

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
						// Bootbox.alert("IMPACT NUMBER:" +
						// voipAccounts.getActivity("PTU-02") );

						updateTable();
					}
				});

		AudioSupervisorSettingsChangedRemoteEvent.subscribe(eventBus,
				new AudioSupervisorSettingsChangedRemoteEvent.Handler() {

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
				updateTable();
			}
		});

		return true;
	}

	private void updateTable() {
		if (ptu == null) {
			return;
		}

		if (fieldName.size() > 3) {
			while (fieldName.size() > 3) {
				fieldName.remove(3);
				classField.remove(3);
			}
		}

		String ptuId = ptu.getName();
		boolean a = (!(voipAccounts.getActivity(ptuId).equals("") || voipAccounts
				.getActivity(ptuId) == null));
		// Bootbox.alert(voipAccounts.getUsername(ptuId)+ ": "
		// +String.valueOf(a));
		if (!(voipAccounts.getActivity(ptuId).equals("") || voipAccounts
				.getActivity(ptuId) == null)) {
			if (conferenceRooms.conferenceOfActivityExist(voipAccounts
					.getActivity(ptuId))) {
				fieldName.add("Conference");
				classField.add(ButtonCell.class);
				fieldName.add("Mute/Unmute");
				classField.add(ButtonCell.class);
				fieldName.add("Kick/Add");
				classField.add(ButtonCell.class);
			} else {
				fieldName.add("Group Call");
				classField.add(ButtonCell.class);
			}
		}
		dataProvider.getList().clear();
		dataProvider.getList().addAll(fieldName);
	}

	@Override
	public boolean update() {
		return false;
	}

}
