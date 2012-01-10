package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.SupervisorSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.Settings;
import ch.cern.atlas.apvs.client.settings.SupervisorSettings;
import ch.cern.atlas.apvs.client.widget.ClickableTextCell;
import ch.cern.atlas.apvs.client.widget.ClickableTextColumn;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

public class PtuView extends VerticalPanel {

	private static NumberFormat format = NumberFormat.getFormat("0.00");

	private RemoteEventBus eventBus;
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private CellTable<String> table = new CellTable<String>();

	private List<Integer> ptuIds;
	private Measurement<Double> last;
	private SortedMap<Integer, Ptu> ptus;
	private Map<String, String> units;

	private SupervisorSettings settings;
	
	private TimeView timeView;
	
	private void init() {
		last = new Measurement<Double>();
		ptus = new TreeMap<Integer, Ptu>();
		units = new HashMap<String, String>();
	}

	public PtuView(ClientFactory clientFactory) {
		eventBus = clientFactory.getEventBus();
		
		init();

		timeView = new TimeView(clientFactory);
		
		add(table);
		add(timeView);

		// name column
		ClickableTextColumn<String> name = new ClickableTextColumn<String>() {
			@Override
			public String getValue(String object) {
				return object;
			}

			@Override
			public void render(Context context, String object,
					SafeHtmlBuilder sb) {
				((ClickableTextCell) getCell()).render(context,
						SafeHtmlUtils.fromSafeConstant(getValue(object)), sb);
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setSortable(true);
		name.setFieldUpdater(new FieldUpdater<String, String>() {
			
			@Override
			public void update(int index, String name, String value) {
				timeView.setMeasurement(name);
			}
		});

		table.addColumn(name, "Name");

		// unit column
		ClickableTextColumn<String> unit = new ClickableTextColumn<String>() {
			@Override
			public String getValue(String object) {
				return units.get(object);
			}

			@Override
			public void render(Context context, String object,
					SafeHtmlBuilder sb) {
				((ClickableTextCell) getCell()).render(context,
						SafeHtmlUtils.fromSafeConstant(getValue(object)), sb);
			}
		};
		unit.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		unit.setFieldUpdater(new FieldUpdater<String, String>() {
			
			@Override
			public void update(int index, String name, String value) {
				timeView.setMeasurement(name);
			}
		});
		
		table.addColumn(unit, "Unit");

		dataProvider.addDataDisplay(table);
		dataProvider.setList(new ArrayList<String>());

		ListHandler<String> columnSortHandler = new ListHandler<String>(dataProvider.getList());
		columnSortHandler.setComparator(name, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1 != null ? o1.compareTo(o2) : -1;
			}
		});
		table.addColumnSortHandler(columnSortHandler);
		table.getColumnSortList().push(name);

		PtuIdsChangedEvent.subscribe(eventBus,
				new PtuIdsChangedEvent.Handler() {

					@Override
					public void onPtuIdsChanged(PtuIdsChangedEvent event) {
						ptuIds = event.getPtuIds();
			
						while (table.getColumnCount() > 2) {
							table.removeColumn(table.getColumnCount()-1);
						}

						if (ptuIds != null) {
							Collections.sort(ptuIds);

							for (Iterator<Map.Entry<Integer, Ptu>> i=ptus.entrySet().iterator(); i.hasNext(); ) {
								Map.Entry<Integer, Ptu> entry = i.next();
								if (ptuIds.contains(entry.getKey())) continue;
								
								i.remove();
							}
														
							for (Iterator<Integer> i = ptuIds.iterator(); i.hasNext(); ) {
								addColumn(i.next());
							}

							eventBus.fireEvent(new RequestRemoteEvent(MeasurementChangedEvent.class));
							
						} else {
							init();
						}
						update();
					}
				});

		MeasurementChangedEvent.subscribe(eventBus,
				new MeasurementChangedEvent.Handler() {

					@Override
					public void onMeasurementChanged(
							MeasurementChangedEvent event) {
						Measurement<Double> measurement = event
								.getMeasurement();
						Integer ptuId = measurement.getPtuId();
						if ((ptuIds == null) || !ptuIds.contains(ptuId)) return;
						
						Ptu ptu = ptus.get(ptuId);
						if (ptu == null) {
							ptu = new Ptu(ptuId);
							ptus.put(ptuId, ptu);
						}

						String name = measurement.getName();
						if (ptu.getMeasurement(name) == null) {
							units.put(name, measurement.getUnit());
							ptu.addMeasurement(measurement);
							last = measurement;
						} else {
							last = ptu.addMeasurement(measurement);
						}

						if (!dataProvider.getList().contains(name)) {
							dataProvider.getList().add(name);
						}
						update();
					}
				});

		SupervisorSettingsChangedEvent.subscribe(eventBus,
				new SupervisorSettingsChangedEvent.Handler() {

					@Override
					public void onSupervisorSettingsChanged(SupervisorSettingsChangedEvent event) {
						settings = event.getSupervisorSettings();
						update();
					}
				});
	}

	private void addColumn(final Integer ptuId) {
		ClickableTextColumn<String> column = new ClickableTextColumn<String>() {
			@Override
			public String getValue(String object) {
				Ptu ptu = ptus.get(ptuId);
				if (ptu == null) {
					return "";
				}
				
				Measurement<Double> m = ptu.getMeasurement(object);
				if (m == null) {
					return "";
				}
				return format.format(m.getValue());
			}

			@Override
			public void render(Context context, String object,
					SafeHtmlBuilder sb) {
				Ptu ptu = ptus.get(ptuId);
				if (ptu == null) {
					((ClickableTextCell) getCell()).render(context, "", sb);
				} else {
					Measurement<Double> m = ptu.getMeasurement(object);
					((ClickableTextCell) getCell()).render(context,
							MeasurementView.decorate(getValue(object), m, last), sb);
				}
			}

		};
		column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		column.setFieldUpdater(new FieldUpdater<String, String>() {
			
			@Override
			public void update(int index, String name, String value) {
				timeView.setMeasurement(ptuId, name);
			}
		});
		
		table.addColumn(column, new TextHeader("") {
			@Override
			public String getValue() {
				String name = settings != null ? settings.getName(Settings.DEFAULT_SUPERVISOR, ptuId) : null;
				return name != null ? name + "<br/>(" + ptuId.toString() + ")"
						: ptuId.toString();
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
