package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.cern.atlas.apvs.client.event.SettingsChangedEvent;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterPtuChangedEvent;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterSerialNumbersChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;

public class SettingsView extends VerticalFlowPanel {

	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private CellTable<String> table = new CellTable<String>();

	private Settings settings;
	private RemoteEventBus eventBus;
	protected List<Integer> ptuIds = new ArrayList<Integer>();
	protected List<Integer> dosimeterSerialNumbers;

    static final String[] settingNames = { "Name", "PTU Id",
			"Dosimeter #", "URL Helmet Camera", "URL Hand Camera",
			"Show Fake Measurements", "Add/Remove" };
	@SuppressWarnings("rawtypes")
	private static final Class[] cellClass = { EditTextCell.class,
			SelectionCell.class, SelectionCell.class, EditTextCell.class,
			EditTextCell.class, CheckboxCell.class, ButtonCell.class };
	@SuppressWarnings("rawtypes")
	private static final Class[] nameClass = { TextCell.class, TextCell.class,
			TextCell.class, TextCell.class, TextCell.class, TextCell.class,
			ButtonCell.class };

	public SettingsView(final RemoteEventBus eventBus) {
		this.eventBus = eventBus;
		add(table);

		// name column, with Add button
		Column<String, Object> name = new Column<String, Object>(
				new EditableCell(nameClass)) {
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
					int column = settings.size();
					settings.put(column, "Name", "Person " + (column + 1));

					fireSettingsChangedEvent(eventBus, settings);

					SettingsView.this.update();
				}
			}
		});
		table.addColumn(name, "Setting");

		dataProvider.addDataDisplay(table);
		dataProvider.setList(Arrays.asList(settingNames));

		SettingsChangedEvent.subscribe(eventBus,
				new SettingsChangedEvent.Handler() {
					@Override
					public void onSettingsChanged(SettingsChangedEvent event) {
						System.err.println("Setting settings !!HDJHJDFHJ");
						if (event.getSource() == this) {
							return;
						}
						settings = event.getSettings();

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
			for (int i = 0; i < settings.size(); i++) {
				addColumn(i);
			}
		}

		table.redraw();
	}

	private void addColumn(final int id) {
		Column<String, Object> column = new Column<String, Object>(
				new EditableCell(cellClass)) {
			@Override
			public Object getValue(String name) {
				Object s = settings.get(id, name);
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

				if (name.equals(settingNames[1])) {
					((EditableCell) getCell())
							.setOptions(new StringList<Integer>(ptuIds));
				} else if (name.equals(settingNames[2])) {
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

				if (name.equals(settingNames[1])) {
					((EditableCell) getCell())
							.setOptions(new StringList<Integer>(ptuIds));
				} else if (name.equals(settingNames[2])) {
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
									+ settings.get(id, settingNames[0]) + "' ?")) {
						settings.remove(id);
						SettingsView.this.update();
						fire = true;
					}
				} else {
					settings.put(id, name, value.toString());
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

    static void fireSettingsChangedEvent(RemoteEventBus eventBus, Settings settings) {
		HashMap<Integer, Integer> dosimeterToPtu = new HashMap<Integer, Integer>();

		// takes the last proper value
		for (Iterator<Map<String, String>> i = settings.getList().iterator(); i
				.hasNext();) {
			Map<String, String> map = i.next();
			String ptuId = map.get(settingNames[1]);
			String dosimeterSerialNo = map.get(settingNames[2]);
			if ((ptuId != null) && (dosimeterSerialNo != null)) {
				dosimeterToPtu.put(Integer.parseInt(dosimeterSerialNo),
						Integer.parseInt(ptuId));
			}
		}

		eventBus.fireEvent(new SettingsChangedEvent(settings));
		eventBus.fireEvent(new DosimeterPtuChangedEvent(dosimeterToPtu));
	}
}
