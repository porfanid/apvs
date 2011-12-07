package ch.cern.atlas.apvs.client;

import java.util.Arrays;

import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

public class SettingsView extends VerticalFlowPanel {

	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private CellTable<String> table = new CellTable<String>();

	private final static String APVS_SETTINGS = "APVS.settings";
	private SettingsFactory settingsFactory = GWT.create(SettingsFactory.class);
	private Settings settings;

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

		settings = settingsFactory.settings().as();

		load();

		insertColumn(1);
		insertColumn(2);

		update();
	}

	private void insertColumn(final int id) {
		int columnIndex = id;
		@SuppressWarnings("unchecked")
		Column<String, String> column = new Column<String, String>(
				new EditableCell(cellClass)) {
			@Override
			public String getValue(String name) {
				String s = settings.get(id, name);
				if (s != null) {
					return s;
				}
				return "";
			}

			@Override
			public void render(Context context, String name, SafeHtmlBuilder sb) {
				String s = getValue(name);
				getCell().render(context, s, sb);
			}
		};

		column.setFieldUpdater(new FieldUpdater<String, String>() {

			@Override
			public void update(int index, String name, String value) {
				System.err.println("Updated " + index + " " + name + " "
						+ value + " for " + id);
				settings.put(id, name, value);

				// FIXME, publish on event bus
				store();
			}
		});

		column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		table.insertColumn(columnIndex, column, Integer.toString(id));
	}

	private void update() {
		table.redraw();
	}

	private void load() {
		Storage store = Storage.getLocalStorageIfSupported();
		if (store == null) {
			Window.alert("Settings will not be stored");
			return;
		}
		System.err.println(Storage.isSupported()+" "+Storage.isLocalStorageSupported());
		System.err.println(store.getLength());
		for (int i = 0; i < store.getLength(); i++) {
			String key = store.key(i);
			System.err.println(key+" "+store.getItem(key));
		}
		String json = store.getItem(APVS_SETTINGS);
		if (json != null) {
			System.err.println("get " + json);
			AutoBean<Settings> bean = AutoBeanCodex.decode(settingsFactory,
					Settings.class, json);
			settings = bean.as();

			System.err.println(settings.debugString());
			
			update();
		} 
	}

	private void store() {
		Storage store = Storage.getLocalStorageIfSupported();
		if (store == null)
			return;

		AutoBean<Settings> bean = AutoBeanUtils.getAutoBean(settings);
		String json = AutoBeanCodex.encode(bean).getPayload();

		System.err.println("set " + json);
		store.setItem(APVS_SETTINGS, json);
		System.err.println(store.getLength());
	}
}
