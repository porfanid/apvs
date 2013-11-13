package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.widget.ActionHeader;
import ch.cern.atlas.apvs.client.widget.AsyncEditTextColumn;
import ch.cern.atlas.apvs.client.widget.AsyncFieldUpdater;
import ch.cern.atlas.apvs.client.widget.ClickableHtmlColumn;
import ch.cern.atlas.apvs.client.widget.ClickableTextColumn;
import ch.cern.atlas.apvs.client.widget.CompositeHeader;
import ch.cern.atlas.apvs.client.widget.GlassPanel;
import ch.cern.atlas.apvs.client.widget.PagerHeader;
import ch.cern.atlas.apvs.client.widget.PagerHeader.TextLocation;
import ch.cern.atlas.apvs.client.widget.ScrolledDataGrid;
import ch.cern.atlas.apvs.client.widget.UpdateScheduler;
import ch.cern.atlas.apvs.domain.ClientConstants;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.MeasurementConfiguration;
import ch.cern.atlas.apvs.domain.SensorOrder;
import ch.cern.atlas.apvs.domain.SortOrder;
import ch.cern.atlas.apvs.domain.Ternary;
import ch.cern.atlas.apvs.event.ConnectionStatusChangedRemoteEvent;
import ch.cern.atlas.apvs.ptu.shared.MeasurementConfigurationChangedEvent;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;

public class MeasurementConfigurationView extends GlassPanel implements Module {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private ScrolledDataGrid<MeasurementConfiguration> table = new ScrolledDataGrid<MeasurementConfiguration>();
	private ScrollPanel scrollPanel;

	private PagerHeader pager;
	private ActionHeader update;
	private boolean showUpdate;

	private String ptuHeader;
	private CompositeHeader compositeFooter;
	private ClickableTextColumn<MeasurementConfiguration> ptu;
	private String nameHeader;
	private ClickableHtmlColumn<MeasurementConfiguration> name;

	private UpdateScheduler scheduler = new UpdateScheduler(this);

	private Ternary databaseConnect = Ternary.Unknown;
	private Ternary daqConnect = Ternary.Unknown;

	public MeasurementConfigurationView() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean configure(Element element,
			final ClientFactory clientFactory, Arguments args) {

		String height = args.getArg(0);

		table.setSize("100%", height);
		table.setEmptyTableWidget(new Label("No Measurement Configuration"));

		pager = new PagerHeader(TextLocation.LEFT);
		pager.setDisplay(table);
		pager.setNextPageButtonsDisabled(true);

		update = new ActionHeader("Update", new Delegate<String>() {
			@Override
			public void execute(String object) {
				pager.setPage(0);
				scrollPanel.setVerticalScrollPosition(scrollPanel
						.getMinimumHorizontalScrollPosition());

				table.getColumnSortList().clear();
				table.getColumnSortList().push(new ColumnSortInfo(ptu, true));
				ColumnSortEvent.fire(table, table.getColumnSortList());
				scheduler.update();
			}
		});
		update.setVisible(false);

		compositeFooter = new CompositeHeader(pager.getHeader(), update) {

			public boolean onPreviewColumnSortEvent(Context context,
					Element elem, NativeEvent event) {
				// events are handled, do not sort, fix for #454
				return false;
			}
		};

		final TextArea msg = new TextArea();
		// FIXME, not sure how to handle scroll bar and paging
		// add(msg, NORTH);

		setWidth("100%");
		add(table, CENTER);

		scrollPanel = table.getScrollPanel();
		scrollPanel.addScrollHandler(new ScrollHandler() {

			int line = 0;

			@Override
			public void onScroll(ScrollEvent event) {
				msg.setText(msg.getText() + "\n" + line + " "
						+ event.toDebugString() + " "
						+ scrollPanel.getVerticalScrollPosition() + " "
						+ scrollPanel.getMinimumVerticalScrollPosition() + " "
						+ scrollPanel.getMaximumVerticalScrollPosition());
				msg.getElement().setScrollTop(
						msg.getElement().getScrollHeight());
				line++;

				if (scrollPanel.getVerticalScrollPosition() == scrollPanel
						.getMinimumVerticalScrollPosition()) {
					scheduler.update();
				}
			}
		});

		AsyncDataProvider<MeasurementConfiguration> dataProvider = new AsyncDataProvider<MeasurementConfiguration>() {

			@Override
			protected void onRangeChanged(
					HasData<MeasurementConfiguration> display) {

				clientFactory.getPtuService().getRowCount(
						new AsyncCallback<Long>() {

							@Override
							public void onSuccess(Long result) {
								table.setRowCount(result.intValue());
							}

							@Override
							public void onFailure(Throwable caught) {
								table.setRowCount(0);
							}
						});

				final Range range = display.getVisibleRange();

				final ColumnSortList sortList = table.getColumnSortList();
				List<SortOrder> order = new ArrayList<SortOrder>(
						sortList.size());
				for (int i = 0; i < sortList.size(); i++) {
					ColumnSortInfo info = sortList.get(i);
					order.add(new SortOrder(info.getColumn()
							.getDataStoreName(), info.isAscending()));
				}

				if (order.isEmpty()) {
					order.add(new SortOrder("device.name", false));
				}

				clientFactory.getPtuService().getTableData(range.getStart(),
						range.getLength(), order,
						new AsyncCallback<List<MeasurementConfiguration>>() {

							@Override
							public void onSuccess(
									List<MeasurementConfiguration> result) {
								table.setRowData(range.getStart(), result);
							}

							@Override
							public void onFailure(Throwable caught) {
								log.warn("RPC DB FAILED " + caught);
								table.setRowCount(0);
							}
						});
			}
		};

		// Table
		dataProvider.addDataDisplay(table);

		AsyncHandler columnSortHandler = new AsyncHandler(table);
		table.addColumnSortHandler(columnSortHandler);

		// Subscriptions
		ConnectionStatusChangedRemoteEvent.subscribe(
				clientFactory.getRemoteEventBus(),
				new ConnectionStatusChangedRemoteEvent.Handler() {

					@Override
					public void onConnectionStatusChanged(
							ConnectionStatusChangedRemoteEvent event) {
						switch (event.getConnection()) {
						case databaseConnect:
							databaseConnect = event.getStatus();
							break;
						case daq:
							daqConnect = event.getStatus();
							break;
						default:
							break;
						}

						showGlass(daqConnect.not().isTrue()
								|| databaseConnect.not().isTrue());
					}
				});

		MeasurementConfigurationChangedEvent.register(
				clientFactory.getRemoteEventBus(),
				new MeasurementConfigurationChangedEvent.Handler() {

					@Override
					public void onMeasurementConfigurationChanged(
							MeasurementConfigurationChangedEvent e) {
						MeasurementConfiguration mc = e
								.getMeasurementConfiguration();
						if (mc == null)
							return;

						showUpdate = true;
						scheduler.update();
					}
				});

		// PtuID (1)
		ptu = new ClickableTextColumn<MeasurementConfiguration>() {
			@Override
			public String getValue(MeasurementConfiguration object) {
				if (object == null) {
					return "";
				}
				return object.getDevice().getName();
			}
		};
		ptu.setDataStoreName("device.name");
		ptu.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		ptu.setSortable(true);
		ptuHeader = "PTU ID";
		table.addColumn(ptu, new TextHeader(ptuHeader), compositeFooter);

		// Name (2)
		name = new ClickableHtmlColumn<MeasurementConfiguration>() {
			@Override
			public String getValue(MeasurementConfiguration object) {
				if (object == null) {
					return "";
				}
				return Measurement.getDisplayName(object.getSensor());
			}
		};
		name.setDataStoreName("sensor");
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setSortable(true);
		nameHeader = "Name";
		table.addColumn(name, new TextHeader(nameHeader), compositeFooter);

		// Value
		ClickableTextColumn<MeasurementConfiguration> value = new ClickableTextColumn<MeasurementConfiguration>() {
			@Override
			public String getValue(MeasurementConfiguration object) {
				if (object == null) {
					return "";
				}

				return "TBD";
			}
		};
		value.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		table.addColumn(value, new TextHeader("Value"), compositeFooter);

		// Unit
		ClickableHtmlColumn<MeasurementConfiguration> unit = new ClickableHtmlColumn<MeasurementConfiguration>() {
			@Override
			public String getValue(MeasurementConfiguration object) {
				if (object == null) {
					return "";
				}

				return object.getUnit();
			}
		};
		unit.setDataStoreName("unit");
		unit.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		unit.setSortable(true);
		table.addColumn(unit, "Unit");

		// DATE and TIME
		ClickableTextColumn<MeasurementConfiguration> time = new ClickableTextColumn<MeasurementConfiguration>() {
			@Override
			public String getValue(MeasurementConfiguration object) {
				if (object == null) {
					return "";
				}
				return ClientConstants.dateFormatNoSeconds.format(object
						.getTime());
			}
		};
		time.setDataStoreName("time");
		time.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		time.setSortable(true);
		table.addColumn(time, "Updated");

		// DownThreshold
		final AsyncEditTextColumn<MeasurementConfiguration> downThreshold = new AsyncEditTextColumn<MeasurementConfiguration>() {
			@Override
			public String getValue(MeasurementConfiguration object) {
				if (object == null) {
					return "";
				}

				return object.getDownThreshold() != null ? object
						.getDownThreshold().toString() : "";
			}
		};
		downThreshold.setDataStoreName("downThreshold");
		downThreshold.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		downThreshold.setSortable(true);
		downThreshold
				.setFieldUpdater(new AsyncFieldUpdater<MeasurementConfiguration, String>() {

					@Override
					public void update(int index,
							MeasurementConfiguration object, String value) {

						final SensorOrder order = new SensorOrder(object.getDevice(), object.getSensor(), 
								"DownThreshold", value);
						clientFactory.getPtuService().handleOrder(
								order,
								downThreshold.getCallback(getContext(),
										object.getDevice(), value));
					}
				});
		downThreshold.setEnabled(clientFactory.isSupervisor());
		table.addColumn(downThreshold, new TextHeader("Down Threshold"));

		// UpThreshold
		final AsyncEditTextColumn<MeasurementConfiguration> upThreshold = new AsyncEditTextColumn<MeasurementConfiguration>() {
			@Override
			public String getValue(MeasurementConfiguration object) {
				if (object == null) {
					return "";
				}

				return object.getUpThreshold() != null ? object
						.getUpThreshold().toString() : "";
			}
		};
		upThreshold.setDataStoreName("upThreshold");
		upThreshold.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		upThreshold.setSortable(true);
		upThreshold
				.setFieldUpdater(new AsyncFieldUpdater<MeasurementConfiguration, String>() {

					@Override
					public void update(int index,
							MeasurementConfiguration object, String value) {
						final SensorOrder order = new SensorOrder(object.getDevice(), object.getSensor(), 
								"UpThreshold", value);
						clientFactory.getPtuService().handleOrder(
								order,
								upThreshold.getCallback(getContext(),
										object.getDevice(), value));
					}
				});
		upThreshold.setEnabled(clientFactory.isSupervisor());
		table.addColumn(upThreshold, new TextHeader("Up Threshold"));

		// Slope
		final AsyncEditTextColumn<MeasurementConfiguration> slope = new AsyncEditTextColumn<MeasurementConfiguration>() {
			@Override
			public String getValue(MeasurementConfiguration object) {
				if (object == null) {
					return "";
				}

				return object.getSlope() != null ? object.getSlope().toString()
						: "";
			}
		};
		slope.setDataStoreName("slope");
		slope.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		slope.setSortable(true);
		slope.setFieldUpdater(new AsyncFieldUpdater<MeasurementConfiguration, String>() {

			@Override
			public void update(int index, MeasurementConfiguration object,
					String value) {
				final SensorOrder order = new SensorOrder(object.getDevice(), object.getSensor(), 
						"Slope", value);
				clientFactory.getPtuService().handleOrder(
						order,
						slope.getCallback(getContext(),
								object.getDevice(), value));
			}
		});
		slope.setEnabled(clientFactory.isSupervisor());
		table.addColumn(slope, new TextHeader("Slope"));

		// Offset
		final AsyncEditTextColumn<MeasurementConfiguration> offset = new AsyncEditTextColumn<MeasurementConfiguration>() {
			@Override
			public String getValue(MeasurementConfiguration object) {
				if (object == null) {
					return "";
				}

				return object.getOffset() != null ? object.getOffset()
						.toString() : "";
			}
		};
		offset.setDataStoreName("offset");
		offset.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		offset.setSortable(true);
		offset.setFieldUpdater(new AsyncFieldUpdater<MeasurementConfiguration, String>() {

			@Override
			public void update(int index, MeasurementConfiguration object,
					String value) {
				final SensorOrder order = new SensorOrder(object.getDevice(), object.getSensor(), 
						"Offset", value);
				clientFactory.getPtuService().handleOrder(
						order,
						offset.getCallback(getContext(),
								object.getDevice(), value));
			}
		});
		offset.setEnabled(clientFactory.isSupervisor());
		table.addColumn(offset, new TextHeader("Offset"));

		return true;
	}

	private boolean needsUpdate() {
		if (showUpdate) {
			return showUpdate;
		}
		return false;
	}

	@Override
	public boolean update() {
		// show or hide update button
		boolean needsUpdate = needsUpdate();
		update.setVisible(needsUpdate);

		// Re-sort the table
		if (!needsUpdate) {
			ColumnSortEvent.fire(table, table.getColumnSortList());
			RangeChangeEvent.fire(table, table.getVisibleRange());
		}
		table.redraw();

		return false;
	}
}
