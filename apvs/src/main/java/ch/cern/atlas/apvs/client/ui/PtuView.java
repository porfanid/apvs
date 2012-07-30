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
import ch.cern.atlas.apvs.client.NamedEventBus;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
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
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.event.shared.EventBus;

public class PtuView extends VerticalPanel {

	private static NumberFormat format = NumberFormat.getFormat("0.00");

	private RemoteEventBus eventBus;
	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private CellTable<String> table = new CellTable<String>();

	private List<Integer> ptuIds;
	private Measurement<Double> last;
	private SortedMap<Integer, Ptu> ptus;
	private Map<String, String> units;
	private SingleSelectionModel<String> selectionModel;
	private Map<Integer, String> colorMap = new HashMap<Integer, String>();
 
	private PtuSettings settings;
	private EventBus cmdBus;
		
	private void init() {
		last = new Measurement<Double>();
		ptus = new TreeMap<Integer, Ptu>();
		units = new HashMap<String, String>();
	}

	public PtuView(ClientFactory clientFactory, Arguments args) {
		eventBus = clientFactory.getRemoteEventBus();
		
		init();
		
		cmdBus = NamedEventBus.get(args.getArg(0));
		
		add(table);

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
				selectMeasurement(name);
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
				selectMeasurement(name);
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
		
		selectionModel = new SingleSelectionModel<String>();
		table.setSelectionModel(selectionModel);

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

							((RemoteEventBus)eventBus).fireEvent(new RequestRemoteEvent(MeasurementChangedEvent.class));
							
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

		PtuSettingsChangedEvent.subscribe(eventBus,
				new PtuSettingsChangedEvent.Handler() {

					@Override
					public void onPtuSettingsChanged(PtuSettingsChangedEvent event) {
						settings = event.getPtuSettings();
						update();
					}
				});
		
		ColorMapChangedEvent.subscribe(cmdBus, new ColorMapChangedEvent.Handler() {
			@Override
			public void onColorMapChanged(ColorMapChangedEvent event) {
				colorMap = event.getColorMap();
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
					String color = colorMap.get(ptuId);
					boolean isSelected = selectionModel.isSelected(object);
					if ((color != null) && isSelected) {
						sb.append(SafeHtmlUtils.fromSafeConstant("<div style=\"background:"+color+"\">"));
					}

					Measurement<Double> m = ptu.getMeasurement(object);
					((ClickableTextCell) getCell()).render(context,
							MeasurementView.decorate(getValue(object), m, last), sb);

					if ((color != null) && isSelected) {
						sb.append(SafeHtmlUtils.fromSafeConstant("</div>"));
					}
				}
			}

		};
		column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		column.setFieldUpdater(new FieldUpdater<String, String>() {
			
			@Override
			public void update(int index, String name, String value) {
				// FIXME should have ptuID set... via SelectPtuEvent
				selectMeasurement(name);
			}
		});
		
		table.addColumn(column, new TextHeader("") {
			@Override
			public String getValue() {
				String name = settings != null ? settings.getName(ptuId) : null;
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
		
		String selection = selectionModel.getSelectedObject();
		if ((selection == null) && (dataProvider.getList().size() > 0)) {
			selection = dataProvider.getList().get(0);
			
			selectMeasurement(selection);
		}
			
		// re-set the selection as the async update may have changed the rendering
		if (selection != null) {
			selectionModel.setSelected(selection, true);
		}
	}

	private void selectMeasurement(String name) {
		SelectMeasurementEvent.fire(cmdBus, name);
	}
}
