package ch.cern.atlas.apvs.client.ui;

import java.util.Arrays;
import java.util.List;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.AudioSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.service.AudioServiceAsync;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.client.widget.EditableCell;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.EventBus;

public class AudioView extends VerticalPanel implements Module {

	private CellTable<String> table = new CellTable<String>();
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private AudioSettings voipAccounts = new AudioSettings();
	private String ptuId = new String("PTU1234");

	private EventBus cmdBus;

	private List<String> fieldName = Arrays.asList(new String[] { "Status",
			"Private Call", "Group Call", "New Group Call" });
	private List<Class<?>> classField = Arrays.asList(new Class<?>[] {
			TextCell.class, ButtonCell.class, ButtonCell.class,
			ButtonCell.class });

	public AudioView() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,
			Arguments args) {
		final RemoteEventBus eventBus = clientFactory.getRemoteEventBus();

		cmdBus = clientFactory.getEventBus(args.getArg(0));

		add(table);

		// Field Name column
		Column<String, String> name = new Column<String, String>(new TextCell()) {

			@Override
			public String getValue(String field) {
				return field;
			}
		};
		name.setHorizontalAlignment(ALIGN_CENTER);
		table.addColumn(name);

		// Field
		EditableCell fieldCell = new EditableCell(classField, 50);

		Column<String, Object> field = new Column<String, Object>(fieldCell) {

			@Override
			public Object getValue(String fieldName) {
				if (fieldName.equals("Status"))
					return voipAccounts.getStatus(ptuId);
				else if (fieldName.equals("Private Call"))
					return "Call '" + voipAccounts.getUsername(ptuId) + "'";
				else if (fieldName.equals("Group Call"))
					return "Join '" + voipAccounts.getActivity(ptuId) + "'";
				else if (fieldName.equals("New Group Call"))
					return "Create '" + voipAccounts.getActivity(ptuId)
							+ "' conference";
				else
					return null;
			}
		};

		field.setFieldUpdater(new FieldUpdater<String, Object>() {

			@Override
			public void update(int index, String fieldName, Object value) {
				if (fieldName.equals("Private Call")) {
					if (voipAccounts.getOnCall(ptuId)) {
						AudioServiceAsync.Util.getInstance().hangup(
								voipAccounts.getChannel(ptuId),
								new AsyncCallback<Void>() {
									@Override
									public void onSuccess(Void result) {
										System.err
												.println("Call Established...");
									}

									@Override
									public void onFailure(Throwable caught) {
										System.err
												.println("Fail to established the call "
														+ caught);
									}
								});

					} else {
						AudioServiceAsync.Util.getInstance().call(
								voipAccounts.getNumber(ptuId), "2000",
								new AsyncCallback<Void>() {

									@Override
									public void onSuccess(Void result) {
										System.err
												.println("Call Established...");
									}

									@Override
									public void onFailure(Throwable caught) {
										System.err
												.println("Fail to established the call "
														+ caught);
									}
								});
					}
				} else if (fieldName.equals("Group Call")) {
					AudioServiceAsync.Util.getInstance().addToConference(
							voipAccounts.getChannel(ptuId), "0",
							new AsyncCallback<Void>() {

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

				} else if (fieldName.equals("New Group Call")) {
					AudioServiceAsync.Util.getInstance().newConference(
							voipAccounts.getNumber(ptuId),
							new AsyncCallback<Void>() {

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

				} else
					return;
			}
		});

		field.setHorizontalAlignment(ALIGN_CENTER);
		table.addColumn(field);

		dataProvider.addDataDisplay(table);
		dataProvider.setList(fieldName);

		if (cmdBus != null) {
			SelectPtuEvent.subscribe(cmdBus, new SelectPtuEvent.Handler() {

				@Override
				public void onPtuSelected(SelectPtuEvent event) {
					ptuId = event.getPtuId();
					System.err.println("NEW PTU " + ptuId);
					table.redraw();
				}
			});
		}

		AudioSettingsChangedEvent.subscribe(eventBus,
				new AudioSettingsChangedEvent.Handler() {

					@Override
					public void onAudioSettingsChanged(
							AudioSettingsChangedEvent event) {
						voipAccounts = event.getAudioSettings();
						table.redraw();
					}
				});

		return true;

	}

}
