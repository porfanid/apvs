package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.client.event.SupervisorSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.PtuSetting;
import ch.cern.atlas.apvs.client.settings.Settings;
import ch.cern.atlas.apvs.client.settings.SupervisorSettings;
import ch.cern.atlas.apvs.client.widget.StringList;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterPtuChangedEvent;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterSerialNumbersChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Shows a list of PTU settings which are alive. A list of ever alive PTU
 * settings is persisted.
 * 
 * @author duns
 * 
 */
public class PtuSettingsView extends VerticalFlowPanel {

	private ListDataProvider<PtuSetting> dataProvider = new ListDataProvider<PtuSetting>();
	private CellTable<PtuSetting> table = new CellTable<PtuSetting>();
	private ListHandler<PtuSetting> columnSortHandler;

	private String supervisor = Settings.DEFAULT_SUPERVISOR;
	private SupervisorSettings settings;
	private RemoteEventBus eventBus;
	protected List<Integer> dosimeterSerialNumbers;

	public PtuSettingsView(final RemoteEventBus eventBus) {
		this.eventBus = eventBus;
		add(table);

		// PTU ID
		Column<PtuSetting, Number> ptuId = new Column<PtuSetting, Number>(
				new NumberCell(NumberFormat.getFormat("0"))) {
			@Override
			public Number getValue(PtuSetting object) {
				return object.getPtuId();
			}
		};
		ptuId.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		ptuId.setSortable(true);
		table.addColumn(ptuId, "PTU ID");

		// ENABLED
		// NAME
		Column<PtuSetting, Boolean> enabled = new Column<PtuSetting, Boolean>(
				new CheckboxCell()) {
			@Override
			public Boolean getValue(PtuSetting object) {
				return object.isEnabled();
			}
		};
		enabled.setFieldUpdater(new FieldUpdater<PtuSetting, Boolean>() {
			
			@Override
			public void update(int index, PtuSetting object, Boolean value) {
				System.err.println("FIXME updated "+value);
			}
		});
		enabled.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		enabled.setSortable(true);
		table.addColumn(enabled, "Enabled");
		
		// NAME
		Column<PtuSetting, String> name = new Column<PtuSetting, String>(
				new EditTextCell()) {
			@Override
			public String getValue(PtuSetting object) {
				return object.getName();
			}
		};
		name.setFieldUpdater(new FieldUpdater<PtuSetting, String>() {
			
			@Override
			public void update(int index, PtuSetting object, String value) {
				System.err.println("FIXME updated "+value);
			}
		});
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setSortable(true);
		table.addColumn(name, "Name");
		
		// DOSIMETER
		Column<PtuSetting, String> dosimeter = new Column<PtuSetting, String>(
				new SelectionCell(new StringList<Integer>(dosimeterSerialNumbers))) {

				@Override
				public String getValue(PtuSetting object) {
					return object.getDosimeterSerialNumber().toString();
				}
			
		};
		dosimeter.setFieldUpdater(new FieldUpdater<PtuSetting, String>() {
			
			@Override
			public void update(int index, PtuSetting object, String value) {
				System.err.println("FIXME updated "+value);
			}
		});
		dosimeter.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dosimeter.setSortable(true);
		table.addColumn(dosimeter, "Dosimeter #");
		
		// HELMET URL
		Column<PtuSetting, String> helmetUrl = new Column<PtuSetting, String>(
				new EditTextCell()) {
			@Override
			public String getValue(PtuSetting object) {
				return object.getHelmetUrl();
			}
		};
		helmetUrl.setFieldUpdater(new FieldUpdater<PtuSetting, String>() {
			
			@Override
			public void update(int index, PtuSetting object, String value) {
				System.err.println("FIXME updated "+value);
			}
		});
		helmetUrl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		helmetUrl.setSortable(true);
		table.addColumn(helmetUrl, "Helmet Camera URL");

		// HELMET URL
		Column<PtuSetting, String> handUrl = new Column<PtuSetting, String>(
				new EditTextCell()) {
			@Override
			public String getValue(PtuSetting object) {
				return object.getHandUrl();
			}
		};
		handUrl.setFieldUpdater(new FieldUpdater<PtuSetting, String>() {
			
			@Override
			public void update(int index, PtuSetting object, String value) {
				System.err.println("FIXME updated "+value);
			}
		});
		handUrl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		handUrl.setSortable(true);
		table.addColumn(helmetUrl, "Hand Camera URL");

		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<PtuSetting>());

		// SORTING
		columnSortHandler = new ListHandler<PtuSetting>(dataProvider.getList());
		columnSortHandler.setComparator(ptuId, new Comparator<PtuSetting>() {
			public int compare(PtuSetting o1, PtuSetting o2) {
				return o1 != null ? o1.compareTo(o2) : -1;
			}
		});
		columnSortHandler.setComparator(enabled, new Comparator<PtuSetting>() {
			public int compare(PtuSetting o1, PtuSetting o2) {
				return o1.isEnabled().compareTo(o2.isEnabled());
			}
		});
		columnSortHandler.setComparator(name, new Comparator<PtuSetting>() {
			public int compare(PtuSetting o1, PtuSetting o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		columnSortHandler.setComparator(dosimeter, new Comparator<PtuSetting>() {
			public int compare(PtuSetting o1, PtuSetting o2) {
				return o1.getDosimeterSerialNumber().compareTo(o2.getDosimeterSerialNumber());
			}
		});
		columnSortHandler.setComparator(helmetUrl, new Comparator<PtuSetting>() {
			public int compare(PtuSetting o1, PtuSetting o2) {
				return o1.getHelmetUrl().compareTo(o2.getHelmetUrl());
			}
		});
		columnSortHandler.setComparator(handUrl, new Comparator<PtuSetting>() {
			public int compare(PtuSetting o1, PtuSetting o2) {
				return o1.getHandUrl().compareTo(o2.getHandUrl());
			}
		});
		table.addColumnSortHandler(columnSortHandler);
		table.getColumnSortList().push(ptuId);

		SupervisorSettingsChangedEvent.subscribe(eventBus,
				new SupervisorSettingsChangedEvent.Handler() {
					@Override
					public void onSupervisorSettingsChanged(
							SupervisorSettingsChangedEvent event) {
						settings = event.getSupervisorSettings();

						update();
					}
				});

		PtuIdsChangedEvent.subscribe(eventBus,
				new PtuIdsChangedEvent.Handler() {

					@Override
					public void onPtuIdsChanged(PtuIdsChangedEvent event) {
						List<Integer> ptuIds = event.getPtuIds();
						for (Iterator<Integer> i = ptuIds.iterator(); i
								.hasNext();) {
							int ptuId = i.next();

							// FIXME check if exists...
							dataProvider.getList().add(new PtuSetting(ptuId));
						}
						update();
					}
				});

		DosimeterSerialNumbersChangedEvent.subscribe(eventBus,
				new DosimeterSerialNumbersChangedEvent.Handler() {

					@Override
					public void onDosimeterSerialNumbersChanged(
							DosimeterSerialNumbersChangedEvent event) {
						dosimeterSerialNumbers = event
								.getDosimeterSerialNumbers();
						System.err.println("DOSI changed");
						update();
					}
				});
	}

	private void update() {
		table.redraw();
	}

	private void fireSettingsChangedEvent(RemoteEventBus eventBus,
			SupervisorSettings settings) {
		HashMap<Integer, Integer> dosimeterToPtu = new HashMap<Integer, Integer>();

		// takes the last proper value
		for (Iterator<Map<String, String>> i = settings.iterator(supervisor); i
				.hasNext();) {
			Map<String, String> map = i.next();
			String ptuId = map.get(SupervisorSettings.workerSettingNames[1]);
			String dosimeterSerialNo = map
					.get(SupervisorSettings.workerSettingNames[2]);
			if ((ptuId != null) && (dosimeterSerialNo != null)) {
				dosimeterToPtu.put(Integer.parseInt(dosimeterSerialNo),
						Integer.parseInt(ptuId));
			}
		}

		eventBus.fireEvent(new SupervisorSettingsChangedEvent(settings));
		eventBus.fireEvent(new DosimeterPtuChangedEvent(dosimeterToPtu));
	}
}
