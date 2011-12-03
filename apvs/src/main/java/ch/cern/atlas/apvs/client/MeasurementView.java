package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.cern.atlas.apvs.client.event.SelectDosimeterEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.domain.Dosimeter;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.dosimeter.shared.DosimeterChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.PtuChangedEvent;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class MeasurementView extends SimplePanel {

	private static NumberFormat format = NumberFormat.getFormat("0.00");

	private ListDataProvider<Measurement<?>> dataProvider = new ListDataProvider<Measurement<?>>();
	private CellTable<Measurement<?>> table = new CellTable<Measurement<?>>();
	private ListHandler<Measurement<?>> columnSortHandler;

	private int dosimeterSerialNo = -1;
	private Dosimeter dosimeter;
	private int ptuId = -1;
	private Ptu ptu;

	public MeasurementView(final RemoteEventBus eventBus) {

		add(table);

		SelectDosimeterEvent.register(eventBus,
				new SelectDosimeterEvent.Handler() {

					private HandlerRegistration registration;

					@Override
					public void onDosimeterSelected(
							final SelectDosimeterEvent event) {
						if (eventBus.getUUID() != event.getEventBusUUID())
							return;

						// unregister any remaining handler
						if (registration != null) {
							registration.removeHandler();
							registration = null;
						}

						// set result
						dosimeterSerialNo = event.getSerialNo();
						dosimeter = null;

						// register a new handler
						if (dosimeterSerialNo >= 0) {
							registration = DosimeterChangedEvent.subscribe(
									eventBus,
									new DosimeterChangedEvent.Handler() {

										@Override
										public void onDosimeterChanged(
												DosimeterChangedEvent event) {
											if (event.getDosimeter()
													.getSerialNo() == dosimeterSerialNo) {
												dosimeter = event
														.getDosimeter();
												update();
											}
										}
									});
						}

						update();
					}
				});

		SelectPtuEvent.register(eventBus, new SelectPtuEvent.Handler() {

			private HandlerRegistration registration;

			@Override
			public void onPtuSelected(final SelectPtuEvent event) {
				if (eventBus.getUUID() != event.getEventBusUUID())
					return;

				// unregister any remaining handler
				if (registration != null) {
					registration.removeHandler();
					registration = null;
				}

				ptuId = event.getPtuId();
				ptu = null;

				// register a new handler
				if (ptuId >= 0) {
					registration = PtuChangedEvent.subscribe(eventBus,
							new PtuChangedEvent.Handler() {

								@Override
								public void onPtuChanged(PtuChangedEvent event) {
									if (event.getPtu().getPtuId() == ptuId) {
										ptu = event.getPtu();
										update();
									}
								}
							});
				}

				update();
			}
		});

		TextColumn<Measurement<?>> name = new TextColumn<Measurement<?>>() {
			@Override
			public String getValue(Measurement<?> object) {
				return object.getName();
			}

			@Override
			public void render(Context context, Measurement<?> object,
					SafeHtmlBuilder sb) {
				String name = getValue(object);
				if (name != null) {
					((TextCell) getCell()).render(context,
							SafeHtmlUtils.fromSafeConstant(name), sb);
				}
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setSortable(true);
		table.addColumn(name, "Name");

		TextColumn<Measurement<?>> value = new TextColumn<Measurement<?>>() {
			@Override
			public String getValue(Measurement<?> object) {
				if (object == null) {
					return "";
				}
				return format.format((Double) object.getValue());
			}

			@Override
			public void render(Context context, Measurement<?> object,
					SafeHtmlBuilder sb) {
				String s = getValue(object);
				// FIXME does not work for dosimeter
				if ((object != null) && (ptu != null)
						&& object.getName().equals(ptu.getLastChanged())) {
					String a;
					switch (ptu.getState()) {
					case NEW:
						a = "&larr;";
						break;
					case UP:
						a = "&uarr;";
						break;
					case DOWN:
						a = "&darr;";
						break;
					default:
						a = "";
						break;
					}
					s = a + "&nbsp;<b>" + s + "</b>";
				}
				((TextCell) getCell()).render(context,
						SafeHtmlUtils.fromSafeConstant(s), sb);
			}

		};
		value.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		table.addColumn(value, "Value");

		TextColumn<Measurement<?>> unit = new TextColumn<Measurement<?>>() {
			@Override
			public String getValue(Measurement<?> object) {
				return object.getUnit();
			}

			@Override
			public void render(Context context, Measurement<?> object,
					SafeHtmlBuilder sb) {
				String unit = getValue(object);
				if (unit != null) {
					((TextCell) getCell()).render(context,
							SafeHtmlUtils.fromSafeConstant(unit), sb);
				}
			}
		};
		unit.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		unit.setSortable(true);
		table.addColumn(unit, "Unit");

		List<Measurement<?>> list = new ArrayList<Measurement<?>>();
		dataProvider.addDataDisplay(table);
		dataProvider.setList(list);

		columnSortHandler = new ListHandler<Measurement<?>>(
				dataProvider.getList());
		columnSortHandler.setComparator(name,
				new Comparator<Measurement<? extends Object>>() {
					public int compare(Measurement<? extends Object> o1,
							Measurement<? extends Object> o2) {
						if (o1 == o2) {
							return 0;
						}

						if (o1 != null) {
							return (o2 != null) ? o1.getName().compareTo(
									o2.getName()) : 1;
						}
						return -1;
					}
				});
		columnSortHandler.setComparator(value,
				new Comparator<Measurement<?>>() {
					public int compare(Measurement<?> o1, Measurement<?> o2) {
						if (o1 == o2) {
							return 0;
						}

						if ((o1 != null)
								&& (o1.getValue() instanceof Comparable<?>)) {
							@SuppressWarnings("unchecked")
							Comparable<Object> value = (Comparable<Object>) o1
									.getValue();
							return (o2 != null) ? value.compareTo(o2.getValue())
									: 1;
						}
						return -1;
					}
				});
		columnSortHandler.setComparator(unit,
				new Comparator<Measurement<? extends Object>>() {
					public int compare(Measurement<? extends Object> o1,
							Measurement<? extends Object> o2) {
						if (o1 == o2) {
							return 0;
						}

						if (o1 != null) {
							return (o2 != null) ? o1.getUnit().compareTo(
									o2.getUnit()) : 1;
						}
						return -1;
					}
				});
		table.addColumnSortHandler(columnSortHandler);
		table.getColumnSortList().push(name);
	}

	private void update() {
		// FIXME maybe a better way
		List<Measurement<?>> list = dataProvider.getList();
		list.clear();

		if (ptu != null) {
			list.addAll(ptu.getMeasurements());
		}

		if (dosimeter != null) {
			list.add(new Measurement<Double>("Radiation Rate", dosimeter
					.getRate(), "&micro;Sv/h"));
			list.add(new Measurement<Double>("Radiation Dose", dosimeter
					.getDose(), "&micro;Sv"));
		}

		// Re-sort the table
		ColumnSortEvent.fire(table, table.getColumnSortList());
		table.redraw();
	}
}
