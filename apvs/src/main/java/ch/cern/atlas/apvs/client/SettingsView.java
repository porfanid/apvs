package ch.cern.atlas.apvs.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;

public class SettingsView extends VerticalFlowPanel {

	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private CellTable<String> table = new CellTable<String>();

	SortedMap<Integer, Setting> settings = new TreeMap<Integer, Setting>();
	Map<Integer, TextColumn<String>> columns = new HashMap<Integer, TextColumn<String>>();

	private static final String[] settingNames = { "Name", "PTU Id",
			"Dosimeter #", "URL Helmet Camera", "URL Hand Camera" };
	@SuppressWarnings("rawtypes")
	private static final Class[] cellClass = { EditTextCell.class,
			SelectionCell.class, SelectionCell.class, EditTextCell.class,
			EditTextCell.class };

	public SettingsView(RemoteEventBus eventBus) {

		add(table);

		// name column
		TextColumn<String> name = new TextColumn<String>() {
			@Override
			public String getValue(String object) {
				return object;
			}

			@Override
			public void render(Context context, String object,
					SafeHtmlBuilder sb) {
				((TextCell) getCell()).render(context,
						SafeHtmlUtils.fromSafeConstant(getValue(object)), sb);
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		table.addColumn(name, "Setting");

		dataProvider.addDataDisplay(table);
		dataProvider.setList(Arrays.asList(settingNames));

		insertColumn();
		insertColumn();

		update();
	}

	private void insertColumn() {
		final int id = settings.size() + 1;
		settings.put(id, new Setting(id));

		int columnIndex = settings.size();
		@SuppressWarnings("unchecked")
		Column<String, String> column = new Column<String, String>(
				new EditableCell(cellClass)) {
			@Override
			public String getValue(String name) {
				Object o = settings.get(id).getSetting(name);
				if (o == null) {
					return "";
				}
				return o.toString();
			}

			@Override
			public void render(Context context, String name, SafeHtmlBuilder sb) {
				String s = getValue(name);
				getCell().render(context, s, sb);
			}

		};
		column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		table.insertColumn(columnIndex, column, Integer.toString(id));
	}

	private void update() {
		table.redraw();
	}
}
