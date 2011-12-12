package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.client.event.SupervisorSettingsChangedEvent;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterPtuChangedEvent;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterSerialNumbersChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;

public class SupervisorSettingsView extends VerticalFlowPanel {

	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private CellTable<String> table = new CellTable<String>();

	private String supervisor = Settings.DEFAULT_SUPERVISOR;
	private SupervisorSettings settings;
	private RemoteEventBus eventBus;
	protected List<Integer> ptuIds = new ArrayList<Integer>();
	protected List<Integer> dosimeterSerialNumbers;

	public SupervisorSettingsView(final RemoteEventBus eventBus) {
		this.eventBus = eventBus;
		add(table);

		// name column, with Add button
		@SuppressWarnings("unchecked")
		Column<String, Object> name = new Column<String, Object>(
				new EditableCell(SupervisorSettings.workerNameClass)) {
			@Override
			public Object getValue(String object) {
				return object;
			}

			@Override
			public void render(Context context, String object,
					SafeHtmlBuilder sb) {
				String value = (String) getValue(object);
				if (context.getIndex() == table.getRowCount() - 1) {
					value = "Add";
				}
				getCell().render(context,
						SafeHtmlUtils.fromSafeConstant(value), sb);
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setFieldUpdater(new FieldUpdater<String, Object>() {

			@Override
			public void update(int index, String object, Object value) {
				if (index == table.getRowCount() - 1) {
					int column = settings.size(supervisor);
					settings.put(supervisor, column, "Name", "Person " + (column + 1));

					fireSettingsChangedEvent(eventBus, settings);

					SupervisorSettingsView.this.update();
				}
			}
		});
		table.addColumn(name, "Setting");

		dataProvider.addDataDisplay(table);
		dataProvider.setList(Arrays.asList(SupervisorSettings.workerSettingNames));

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
		// remove all setting columns
		while (table.getColumnCount() > 1) {
			table.removeColumn(table.getColumnCount() - 1);
		}

		if (settings != null) {
			for (int i = 0; i < settings.size(supervisor); i++) {
				addColumn(i);
			}
		}

		table.redraw();
	}

	private void addColumn(final int id) {
		@SuppressWarnings("unchecked")
		Column<String, Object> column = new Column<String, Object>(
				new EditableCell(SupervisorSettings.workerCellClass)) {
			@Override
			public Object getValue(String name) {
				Object s = settings.get(supervisor, id, name);
				if (s != null) {
					return s;
				}
				return "";
			}

			@Override
			public void render(Context context, String name, SafeHtmlBuilder sb) {
				Object s = getValue(name);
				if (context.getIndex() == table.getRowCount() - 1) {
					s = "Delete";
				}

				if (name.equals(SupervisorSettings.workerSettingNames[1])) {
					((EditableCell) getCell())
							.setOptions(new StringList<Integer>(ptuIds));
				} else if (name.equals(SupervisorSettings.workerSettingNames[2])) {
					((EditableCell) getCell())
							.setOptions(new StringList<Integer>(
									dosimeterSerialNumbers));
				}

				getCell().render(context, s, sb);

				((EditableCell) getCell()).setOptions(null);
			}

			@Override
			public void onBrowserEvent(Context context, Element elem,
					String name, NativeEvent event) {

				if (name.equals(SupervisorSettings.workerSettingNames[1])) {
					((EditableCell) getCell())
							.setOptions(new StringList<Integer>(ptuIds));
				} else if (name.equals(SupervisorSettings.workerSettingNames[2])) {
					((EditableCell) getCell())
							.setOptions(new StringList<Integer>(
									dosimeterSerialNumbers));
				}

				super.onBrowserEvent(context, elem, name, event);

				((EditableCell) getCell()).setOptions(null);
			}
		};

		column.setFieldUpdater(new FieldUpdater<String, Object>() {

			@Override
			public void update(int index, String name, Object value) {
				System.err.println("Updated " + index + " " + name + " "
						+ value + " for " + id);
				boolean fire = false;
				if (index == table.getRowCount() - 1) {
					if (Window
							.confirm("Are you sure you want to delete setting "
									+ id + " with name '"
									+ settings.get(supervisor, id, SupervisorSettings.workerSettingNames[0]) + "' ?")) {
						settings.remove(supervisor, id);
						SupervisorSettingsView.this.update();
						fire = true;
					}
				} else {
					settings.put(supervisor, id, name, value.toString());
					fire = true;
				}

				if (fire) {
					fireSettingsChangedEvent(eventBus, settings);
				}
			}
		});

		column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		table.addColumn(column, Integer.toString(id + 1));
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
