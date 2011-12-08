package ch.cern.atlas.apvs.client;

import java.util.Arrays;

import ch.cern.atlas.apvs.client.event.SettingsChangedEvent;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
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

	private static final String[] settingNames = { "Name", "PTU Id",
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

					eventBus.fireEvent(new SettingsChangedEvent(settings));

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
						if (event.getSource() == this)
							return;

						settings = event.getSettings();

						update();
					}
				});
	}

	private void update() {
		// remove all setting columns
		while (table.getColumnCount() > 1) {
			table.removeColumn(table.getColumnCount() - 1);
		}

		for (int i = 0; i < settings.size(); i++) {
			addColumn(i);
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
				getCell().render(context, s, sb);
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
							.confirm("Are you sur you want to delete setting "
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
					eventBus.fireEvent(new SettingsChangedEvent(settings));
				}
			}
		});

		column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		table.addColumn(column, Integer.toString(id + 1));
	}
}
