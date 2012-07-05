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
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterPtuChangedEvent;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterSerialNumbersChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;

public class PtuSettingsView extends VerticalFlowPanel {

	private ListDataProvider<PtuSetting> dataProvider = new ListDataProvider<PtuSetting>();
	private CellTable<PtuSetting> table = new CellTable<PtuSetting>();
	private ListHandler<PtuSetting> columnSortHandler;

	private String supervisor = Settings.DEFAULT_SUPERVISOR;
	private SupervisorSettings settings;
	private RemoteEventBus eventBus;
	protected List<Integer> ptuIds = new ArrayList<Integer>();
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
		
		// NAME
		TextColumn<PtuSetting> name = new TextColumn<PtuSetting>() {
			@Override
			public String getValue(PtuSetting object) {
				return object.getName();
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setSortable(true);
		table.addColumn(name, "Name");

		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<PtuSetting>());

		// SORTING
		columnSortHandler = new ListHandler<PtuSetting>(dataProvider.getList());
		columnSortHandler.setComparator(ptuId, new Comparator<PtuSetting>() {
			public int compare(PtuSetting o1, PtuSetting o2) {
				return o1 != null ? o1.compareTo(o2) : -1;
			}
		});
		columnSortHandler.setComparator(name, new Comparator<PtuSetting>() {
			public int compare(PtuSetting o1, PtuSetting o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		table.addColumnSortHandler(columnSortHandler);
		table.getColumnSortList().push(ptuId);
		
		SupervisorSettingsChangedEvent.subscribe(eventBus,
				new SupervisorSettingsChangedEvent.Handler() {
					@Override
					public void onSupervisorSettingsChanged(SupervisorSettingsChangedEvent event) {
						settings = event.getSupervisorSettings();

						update();
					}
				});

		PtuIdsChangedEvent.subscribe(eventBus,
				new PtuIdsChangedEvent.Handler() {

					@Override
					public void onPtuIdsChanged(PtuIdsChangedEvent event) {
						ptuIds = event.getPtuIds();
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
			String dosimeterSerialNo = map.get(SupervisorSettings.workerSettingNames[2]);
			if ((ptuId != null) && (dosimeterSerialNo != null)) {
				dosimeterToPtu.put(Integer.parseInt(dosimeterSerialNo),
						Integer.parseInt(ptuId));
			}
		}

		eventBus.fireEvent(new SupervisorSettingsChangedEvent(settings));
		eventBus.fireEvent(new DosimeterPtuChangedEvent(dosimeterToPtu));
	}
}
