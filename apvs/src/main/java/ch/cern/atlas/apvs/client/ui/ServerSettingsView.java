package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.client.widget.EditableCell;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;

public class ServerSettingsView extends VerticalFlowPanel {

	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private CellTable<String> table = new CellTable<String>();

	private ServerSettings settings = new ServerSettings();

	public ServerSettingsView(ClientFactory factory, Arguments args) {
		final RemoteEventBus eventBus = factory.getRemoteEventBus();
		
		add(table);

		// name column
		@SuppressWarnings("unchecked")
		Column<String, Object> name = new Column<String, Object>(
				new EditableCell(ServerSettings.nameClass, 30)) {
			@Override
			public Object getValue(String object) {
				return object;
			}

			@Override
			public void render(Context context, String object,
					SafeHtmlBuilder sb) {
				String value = (String) getValue(object);
				getCell().render(context,
						SafeHtmlUtils.fromSafeConstant(value), sb);
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		table.addColumn(name, "Server Setting");

		@SuppressWarnings("unchecked")
		Column<String, Object> column = new Column<String, Object>(
				new EditableCell(ServerSettings.cellClass, 50)) {
			@Override
			public Object getValue(String name) {
				return settings.get(name);
			}

			@Override
			public void render(Context context, String name, SafeHtmlBuilder sb) {
				Object s = getValue(name);
				getCell().render(context, s, sb);
			}

			@Override
			public void onBrowserEvent(Context context, Element elem,
					String name, NativeEvent event) {
				super.onBrowserEvent(context, elem, name, event);
			}
		};

		column.setFieldUpdater(new FieldUpdater<String, Object>() {

			@Override
			public void update(int index, String name, Object value) {
				System.err.println("Updated " + index + " " + name + " "
						+ value+" "+value.getClass());
				settings.put(name, value.toString());
				((RemoteEventBus)eventBus).fireEvent(new ServerSettingsChangedEvent(settings));
			}
		});

		column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		table.addColumn(column, "Value");

		dataProvider.addDataDisplay(table);
		dataProvider.setList(ServerSettings.Key.getKeys());
		
		ServerSettingsChangedEvent.subscribe(eventBus,
				new ServerSettingsChangedEvent.Handler() {
					@Override
					public void onServerSettingsChanged(
							ServerSettingsChangedEvent event) {
						System.err.println("Server Settings !!!");
						
						settings = event.getServerSettings();
						
						System.err.println(settings);

						update();
					}
				});
	}

	private void update() {
		table.redraw();
	}
}
