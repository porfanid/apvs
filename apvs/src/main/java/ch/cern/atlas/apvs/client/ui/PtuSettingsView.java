package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.AudioUsersSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.AudioUsersStatusRemoteEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.AudioSettings;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.client.settings.VoipAccount;
import ch.cern.atlas.apvs.client.widget.AsyncEditTextColumn;
import ch.cern.atlas.apvs.client.widget.AsyncFieldUpdater;
import ch.cern.atlas.apvs.client.widget.DynamicSelectionCell;
import ch.cern.atlas.apvs.client.widget.GlassPanel;
import ch.cern.atlas.apvs.client.widget.StringList;
import ch.cern.atlas.apvs.client.widget.TextInputSizeCell;
import ch.cern.atlas.apvs.client.widget.UpdateScheduler;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.Intervention;
import ch.cern.atlas.apvs.domain.InterventionMap;
import ch.cern.atlas.apvs.domain.Order;
import ch.cern.atlas.apvs.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Shows a list of PTU settings which are alive. A list of ever alive PTU
 * settings is persisted.
 * 
 * @author duns
 * 
 */
public class PtuSettingsView extends GlassPanel implements Module {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private ListDataProvider<Device> dataProvider = new ListDataProvider<Device>();
	private CellTable<Device> table = new CellTable<Device>();
	private ListHandler<Device> columnSortHandler;

	protected PtuSettings settings = new PtuSettings();
	protected InterventionMap interventions = new InterventionMap();
	protected List<String> dosimeterSerialNumbers = new ArrayList<String>();

	private UpdateScheduler scheduler = new UpdateScheduler(this);

	private List<VoipAccount> usersAccounts = new ArrayList<VoipAccount>();
	private List<String> usersList = new ArrayList<String>();
	private AudioSettings voipAccounts = new AudioSettings();

	public PtuSettingsView() {
	}

	@Override
	public boolean configure(Element element,
			final ClientFactory clientFactory, Arguments args) {

		final RemoteEventBus eventBus = clientFactory.getRemoteEventBus();

		// Call RPC Method to List SIP Accounts
		clientFactory.getAudioService().usersList(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				System.err.println("Workers SIP accounts listed...");
			}

			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Fail to list workers SIP accounts "
						+ caught);
			}
		});

		add(table, CENTER);

		setVisible(clientFactory.isSupervisor());

		// ENABLED
		Column<Device, Boolean> enabled = new Column<Device, Boolean>(
				new CheckboxCell()) {
			@Override
			public Boolean getValue(Device object) {
				return settings.isEnabled(object.getName());
			}
		};
		enabled.setFieldUpdater(new FieldUpdater<Device, Boolean>() {

			@Override
			public void update(int index, Device object, Boolean value) {
				settings.setEnabled(object.getName(), value);
				fireSettingsChangedEvent(eventBus, settings);
			}
		});
		enabled.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		enabled.setSortable(true);
		table.addColumn(enabled, "Enabled");

		// PTU ID
		Column<Device, String> ptuId = new Column<Device, String>(
				new TextCell()) {
			@Override
			public String getValue(Device object) {
				return object.getName();
			}
		};
		ptuId.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		ptuId.setSortable(true);
		table.addColumn(ptuId, "PTU ID");

		// NAME
		Column<Device, String> name = new Column<Device, String>(new TextCell()) {
			@Override
			public String getValue(Device device) {
				return interventions.get(device) != null ? interventions.get(
						device).getName() : "";
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setSortable(true);
		table.addColumn(name, "Name");

		// Impact Activity Name
		boolean showImpactNumber = false;
		if (showImpactNumber) {
			Column<Device, String> impact = new Column<Device, String>(
					new TextCell()) {

				@Override
				public String getValue(Device object) {
					return interventions.get(object) != null ? interventions
							.get(object).getImpactNumber() : "";
				}
			};
			impact.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			table.addColumn(impact, "Impact #");
		}

		// DOSIMETER
		final AsyncEditTextColumn<Device> dosimeter = new AsyncEditTextColumn<Device>() {
			@Override
			public String getValue(Device device) {
				return settings.getDosimeterSerialNumber(device.getName());
			}
			
			@Override
			protected void onSuccess(Context context, Device device, String value,
					Void result) {
				super.onSuccess(context, device, value, result);
// FIXME, these may just come in from the PTU async
				settings.setDosimeterSerialNumber(
						device.getName(), value);
				fireSettingsChangedEvent(eventBus, settings);
				
				table.redrawRow(context.getIndex());
			}
			
			@Override
			protected void onFailure(Context context, Device device, String value,
					Throwable caught) {
				super.onFailure(context, device, value, caught);

				table.redrawRow(context.getIndex());
			}
		};
				
		if (clientFactory.isSupervisor()) {
			dosimeter.setFieldUpdater(new AsyncFieldUpdater<Device, String>() {

				@Override
				public void update(int index, Device device, String value) {
					
					final Order order = new Order(device, "DosimeterID", value);
					clientFactory.getPtuService().handleOrder(order, dosimeter.getCallback(getContext(), device, value));
				}
			});
		}
		dosimeter.setEnabled(clientFactory.isSupervisor());
		dosimeter.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dosimeter.setSortable(false);
		table.addColumn(dosimeter, "Dosimeter");

		// WIRELESS
		Column<Device, String> bssid = new Column<Device, String>(
				new TextCell()) {
			@Override
			public String getValue(Device object) {
				return settings.getBSSID(object.getName());
			}
		};
		bssid.setSortable(false);
		bssid.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		table.addColumn(bssid, "Wireless");

		// HELMET URL
		Column<Device, String> helmetUrl = new Column<Device, String>(
				new TextInputSizeCell(50)) {
			@Override
			public String getValue(Device object) {
				return settings.getCameraUrl(object.getName(),
						CameraView.HELMET, clientFactory.getProxy());
			}
		};
		helmetUrl.setFieldUpdater(new FieldUpdater<Device, String>() {

			@Override
			public void update(int index, Device object, String value) {
				settings.setCameraUrl(object.getName(), CameraView.HELMET,
						value, clientFactory.getProxy());
				fireSettingsChangedEvent(eventBus, settings);
			}
		});
		helmetUrl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		helmetUrl.setSortable(true);
		table.addColumn(helmetUrl, "Helmet Camera URL");

		// HAND URL
		Column<Device, String> handUrl = new Column<Device, String>(
				new TextInputSizeCell(50)) {
			@Override
			public String getValue(Device object) {
				return settings.getCameraUrl(object.getName(), CameraView.HAND,
						clientFactory.getProxy());
			}
		};
		handUrl.setFieldUpdater(new FieldUpdater<Device, String>() {

			@Override
			public void update(int index, Device object, String value) {
				settings.setCameraUrl(object.getName(), CameraView.HAND, value,
						clientFactory.getProxy());
				fireSettingsChangedEvent(eventBus, settings);
			}
		});
		handUrl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		handUrl.setSortable(true);
		table.addColumn(handUrl, "Hand Camera URL");

		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<Device>());

		// SIP Number
		Column<Device, String> number = new Column<Device, String>(
				new DynamicSelectionCell(new StringList<String>(usersList))) {

			@Override
			public String getValue(Device object) {
				return voipAccounts.getNumber(object.getName());
			}

		};
		number.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(number, "SIP Account");

		number.setFieldUpdater(new FieldUpdater<Device, String>() {

			@Override
			public void update(int index, Device object, String value) {
				if (voipAccounts.contains(object.getName())) {
					voipAccounts.setNumber(object.getName(), value);
					eventBus.fireEvent(new AudioUsersSettingsChangedRemoteEvent(
							voipAccounts));
				}
			}
		});

		// SIP Status
		Column<Device, String> status = new Column<Device, String>(
				new TextCell()) {

			@Override
			public String getValue(Device object) {
				for (int i = 0; i < usersAccounts.size(); i++) {
					if (usersAccounts.get(i).getAccount()
							.equals(voipAccounts.getNumber(object.getName())))
						return (voipAccounts.getStatus(object.getName())/*
																		 * usersAccounts
																		 * .get(
																		 * i).
																		 * getStatus
																		 * ()
																		 */? "Online"
								: "Offline");
				}
				return "Not assigned";
			}

			@Override
			public void render(Context context, Device object,
					SafeHtmlBuilder sb) {
				String value = getValue(object);

				sb.append(SafeHtmlUtils.fromSafeConstant("<div class=\""
						+ value.toLowerCase() + "\">"));
				getCell().render(context, value, sb);
				sb.append(SafeHtmlUtils.fromSafeConstant("</div>"));
			}
		};
		status.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		table.addColumn(status, "Account Status");

		// SORTING
		columnSortHandler = new ListHandler<Device>(dataProvider.getList());
		columnSortHandler.setComparator(ptuId, new Comparator<Device>() {
			public int compare(Device o1, Device o2) {
				return o1 != null ? o1.compareTo(o2) : -1;
			}
		});
		columnSortHandler.setComparator(enabled, new Comparator<Device>() {
			public int compare(Device o1, Device o2) {
				return settings.isEnabled(o1.getName()).compareTo(
						settings.isEnabled(o2.getName()));
			}
		});
		columnSortHandler.setComparator(name, new Comparator<Device>() {
			public int compare(Device o1, Device o2) {
				Intervention i1 = interventions.get(o1);
				Intervention i2 = interventions.get(o2);

				if ((i1 == null) && (i2 == null)) {
					return 0;
				}

				if (i1 == null) {
					return -1;
				}

				if (i2 == null) {
					return 1;
				}

				return i1.getName().compareTo(i2.getName());
			}
		});
		columnSortHandler.setComparator(dosimeter, new Comparator<Device>() {
			public int compare(Device o1, Device o2) {
				return settings
						.getDosimeterSerialNumber(o1.getName())
						.compareTo(
								settings.getDosimeterSerialNumber(o2.getName()));
			}
		});
		columnSortHandler.setComparator(helmetUrl, new Comparator<Device>() {
			public int compare(Device o1, Device o2) {
				return settings.getCameraUrl(o1.getName(), CameraView.HELMET,
						clientFactory.getProxy()).compareTo(
						settings.getCameraUrl(o2.getName(), CameraView.HELMET,
								clientFactory.getProxy()));
			}
		});
		columnSortHandler.setComparator(handUrl, new Comparator<Device>() {
			public int compare(Device o1, Device o2) {
				return settings.getCameraUrl(o1.getName(), CameraView.HAND,
						clientFactory.getProxy()).compareTo(
						settings.getCameraUrl(o2.getName(), CameraView.HAND,
								clientFactory.getProxy()));
			}
		});
		table.addColumnSortHandler(columnSortHandler);
		table.getColumnSortList().push(ptuId);

		PtuSettingsChangedRemoteEvent.subscribe(eventBus,
				new PtuSettingsChangedRemoteEvent.Handler() {
					@Override
					public void onPtuSettingsChanged(
							PtuSettingsChangedRemoteEvent event) {
						log.info("PTU Settings changed");
						settings = event.getPtuSettings();
						scheduler.update();
					}
				});

		InterventionMapChangedRemoteEvent.subscribe(eventBus,
				new InterventionMapChangedRemoteEvent.Handler() {

					@Override
					public void onInterventionMapChanged(
							InterventionMapChangedRemoteEvent event) {
						interventions = event.getInterventionMap();

						dataProvider.getList().clear();
						dataProvider.getList().addAll(interventions.getPtus());

						scheduler.update();
					}
				});

		AudioUsersSettingsChangedRemoteEvent.subscribe(eventBus,
				new AudioUsersSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onAudioUsersSettingsChanged(
							AudioUsersSettingsChangedRemoteEvent event) {
						voipAccounts = event.getAudioSettings();
						scheduler.update();
						// dataProvider.getList().clear();
						// dataProvider.getList().addAll(voipAccounts.getPtuIds());
					}
				});

		AudioUsersStatusRemoteEvent.register(eventBus,
				new AudioUsersStatusRemoteEvent.Handler() {

					@Override
					public void onAudioUsersStatusChange(
							AudioUsersStatusRemoteEvent event) {
						usersAccounts = event.getUsersList();
						usersList.clear();
						for (int i = 0; i < usersAccounts.size(); i++) {
							usersList.add(usersAccounts.get(i).getAccount());
						}
						scheduler.update();
					}
				});

		// DosimeterSerialNumbersChangedEvent.subscribe(eventBus,
		// new DosimeterSerialNumbersChangedEvent.Handler() {
		//
		// @Override
		// public void onDosimeterSerialNumbersChanged(
		// DosimeterSerialNumbersChangedEvent event) {
		// dosimeterSerialNumbers.clear();
		// dosimeterSerialNumbers.addAll(event
		// .getDosimeterSerialNumbers());
		// log.info("DOSI changed "
		// + dosimeterSerialNumbers.size());
		//
		// // FIXME, allow for setting not available as DOSI #
		// scheduler.update();
		// }
		// });

		scheduler.update();

		return true;
	}

	@Override
	public boolean update() {
		// Resort the table
		ColumnSortEvent.fire(table, table.getColumnSortList());

		table.redraw();

		return false;
	}

	private void fireSettingsChangedEvent(EventBus eventBus,
			PtuSettings settings) {

		((RemoteEventBus) eventBus)
				.fireEvent(new PtuSettingsChangedRemoteEvent(settings));
	}
}
