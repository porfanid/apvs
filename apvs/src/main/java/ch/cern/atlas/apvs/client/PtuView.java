package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ch.cern.atlas.apvs.client.event.SettingsChangedEvent;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;

public class PtuView extends VerticalFlowPanel {

	private static NumberFormat format = NumberFormat.getFormat("0.00");

	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private CellTable<String> table = new CellTable<String>();
	private ListHandler<String> columnSortHandler;

	Measurement<Double> last = new Measurement<Double>();
	SortedMap<Integer, Ptu> ptus = new TreeMap<Integer, Ptu>();
	Map<String, String> units = new HashMap<String, String>();
	Map<Integer, TextColumn<String>> columns = new HashMap<Integer, TextColumn<String>>();

	protected Settings settings;

	public PtuView(RemoteEventBus eventBus) {

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
		name.setSortable(true);
		table.addColumn(name, "Name");

		// unit column
		TextColumn<String> unit = new TextColumn<String>() {
			@Override
			public String getValue(String object) {
				return units.get(object);
			}

			@Override
			public void render(Context context, String object,
					SafeHtmlBuilder sb) {
				((TextCell) getCell()).render(context,
						SafeHtmlUtils.fromSafeConstant(getValue(object)), sb);
			}
		};
		unit.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		table.addColumn(unit, "Unit");

		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<String>());

		columnSortHandler = new ListHandler<String>(dataProvider.getList());
		columnSortHandler.setComparator(name, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1 != null ? o1.compareTo(o2) : -1;
			}
		});
		table.addColumnSortHandler(columnSortHandler);
		table.getColumnSortList().push(name);

		MeasurementChangedEvent.subscribe(eventBus,
				new MeasurementChangedEvent.Handler() {

					@Override
					public void onMeasurementChanged(
							MeasurementChangedEvent event) {
						Measurement<Double> measurement = event
								.getMeasurement();
						Integer ptuId = measurement.getPtuId();
						Ptu ptu = ptus.get(ptuId);
						if (ptu == null) {
							ptu = new Ptu(ptuId);
							ptus.put(ptuId, ptu);
							insertColumn(ptuId);
						}

						String name = measurement.getName();
						if (ptu.getMeasurement(name) == null) {
							units.put(name, measurement.getUnit());
							ptu.add(measurement);
							last = measurement;
						} else {
							last = ptu.setMeasurement(name, measurement);
						}

						if (!dataProvider.getList().contains(name)) {
							dataProvider.getList().add(name);
						}
						update();
					}
				});
		
		SettingsChangedEvent.subscribe(eventBus, new SettingsChangedEvent.Handler() {
			
			@Override
			public void onSettingsChanged(SettingsChangedEvent event) {
				settings = event.getSettings();
				update();
			}
		});
	}

	private void insertColumn(final Integer ptuId) {
		int columnIndex = new ArrayList<Integer>(ptus.keySet()).indexOf(ptuId) + 2;
		TextColumn<String> column = new TextColumn<String>() {
			@Override
			public String getValue(String object) {
				Measurement<Double> m = ptus.get(ptuId).getMeasurement(object);
				if (m == null) {
					return "";
				}
				return format.format(m.getValue());
			}

			@Override
			public void render(Context context, String object,
					SafeHtmlBuilder sb) {
				Measurement<Double> m = ptus.get(ptuId).getMeasurement(object);
				String s = getValue(object);
				((TextCell) getCell()).render(context,
						MeasurementView.decorate(s, m, last), sb);
			}

		};
		column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		table.insertColumn(columnIndex, column, new TextHeader("") {
			@Override
			public String getValue() {
				String name = settings.getName(ptuId);
				return name != null ? name+"<br/>("+ptuId.toString()+")" : ptuId.toString();
			}
			
			@Override
			public void render(Context context, SafeHtmlBuilder sb) {
				((TextCell) getCell()).render(context,
						SafeHtmlUtils.fromSafeConstant(getValue()), sb);
			}
		});
	}

	private void update() {
		ColumnSortEvent.fire(table, table.getColumnSortList());
		table.redraw();
	}
}
