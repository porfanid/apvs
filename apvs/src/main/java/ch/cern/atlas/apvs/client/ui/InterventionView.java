package ch.cern.atlas.apvs.client.ui;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.service.InterventionServiceAsync;
import ch.cern.atlas.apvs.client.service.SortOrder;
import ch.cern.atlas.apvs.client.widget.ClickableHtmlColumn;
import ch.cern.atlas.apvs.client.widget.ClickableTextColumn;
import ch.cern.atlas.apvs.client.widget.DataStoreName;
import ch.cern.atlas.apvs.client.widget.EditableCell;
import ch.cern.atlas.apvs.client.widget.GenericColumn;
import ch.cern.atlas.apvs.ptu.shared.PtuClientConstants;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class InterventionView extends SimplePanel implements Module {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private DataGrid<Intervention> table = new DataGrid<Intervention>();

	private boolean selectable = false;
	private boolean sortable = true;

	private final String END_INTERVENTION = "End Intervention";

	public InterventionView() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,
			Arguments args) {

		String height = args.getArg(0);

		table.setSize("100%", height);
		table.setEmptyTableWidget(new Label("No Interventions"));
		table.setVisibleRange(0, 10);

		add(table);

		AsyncDataProvider<Intervention> dataProvider = new AsyncDataProvider<Intervention>() {

			@SuppressWarnings("unchecked")
			@Override
			protected void onRangeChanged(HasData<Intervention> display) {
				InterventionServiceAsync.Util.getInstance().getRowCount(
						new AsyncCallback<Integer>() {

							@Override
							public void onSuccess(Integer result) {
								table.setRowCount(result);
							}

							@Override
							public void onFailure(Throwable caught) {
								table.setRowCount(0);
							}
						});

				final Range range = display.getVisibleRange();
				System.err.println(range);

				final ColumnSortList sortList = table.getColumnSortList();
				SortOrder[] order = new SortOrder[sortList.size()];
				for (int i = 0; i < sortList.size(); i++) {
					ColumnSortInfo info = sortList.get(i);
					// FIXME #88 remove cast
					order[i] = new SortOrder(
							((DataStoreName) info.getColumn())
									.getDataStoreName(),
							info.isAscending());
				}

				if (order.length == 0) {
					order = new SortOrder[1];
					order[0] = new SortOrder("tbl_inspections.endtime", false);
				}

				InterventionServiceAsync.Util.getInstance().getTableData(range,
						order, new AsyncCallback<List<Intervention>>() {

							@Override
							public void onSuccess(List<Intervention> result) {
								System.err.println("RPC DB SUCCESS");
								table.setRowData(range.getStart(), result);
							}

							@Override
							public void onFailure(Throwable caught) {
								System.err.println("RPC DB FAILED");
								table.setRowCount(0);
							}
						});
			}
		};

		// Table
		dataProvider.addDataDisplay(table);

		AsyncHandler columnSortHandler = new AsyncHandler(table);
		table.addColumnSortHandler(columnSortHandler);

		// startTime
		ClickableTextColumn<Intervention> startTime = new ClickableTextColumn<Intervention>() {
			@Override
			public String getValue(Intervention object) {
				return PtuClientConstants.dateFormat.format(object
						.getStartTime());
			}

			@Override
			public String getDataStoreName() {
				return "tbl_inspections.starttime";
			}
		};
		startTime.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		startTime.setSortable(sortable);
		if (selectable) {
			startTime.setFieldUpdater(new FieldUpdater<Intervention, String>() {

				@Override
				public void update(int index, Intervention object, String value) {
					selectIntervention(object);
				}
			});
		}
		Header<String> interventionFooter = new Header<String>(new ButtonCell()) {
			@Override
			public String getValue() {
				return "Add Intervention";
			}
		};
		interventionFooter.setUpdater(new ValueUpdater<String>() {
			
			@Override
			public void update(String value) {
				InterventionServiceAsync.Util.getInstance().addIntervention(42, 43, "Test Intervention", new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						InterventionView.this.update();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						log.warn("Failed");
					}
				});
			}
		});
		table.addColumn(startTime, new TextHeader("Start Time"), interventionFooter);

		// endTime
		EditableCell cell = new EditableCell() {
			@Override
			protected Class<? extends Cell<? extends Object>> getCellClass(
					Context context, Object value) {
				return value == END_INTERVENTION ? ButtonCell.class
						: TextCell.class;
			}
		};
		Column<Intervention, Object> endTime = new GenericColumn<Intervention>(
				cell) {
			@Override
			public String getValue(Intervention object) {
				return object.getEndTime() != null ? PtuClientConstants.dateFormat
						.format(object.getEndTime()) : END_INTERVENTION;
			}

			@Override
			public String getDataStoreName() {
				return "tbl_inspections.starttime";
			}
		};
		endTime.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		endTime.setSortable(sortable);
		endTime.setFieldUpdater(new FieldUpdater<Intervention, Object>() {

			@Override
			public void update(int index, Intervention object, Object value) {
				// FIXME #176 write to DB
				System.err.println("**** "+index+" "+object+" "+value);
			}
		});
		table.addColumn(endTime, "End Time");
		// twice for descending
		table.getColumnSortList().push(endTime);
		table.getColumnSortList().push(endTime);
		
		// Name
		ClickableHtmlColumn<Intervention> name = new ClickableHtmlColumn<Intervention>() {
			@Override
			public String getValue(Intervention object) {
				return object.getName();
			}

			@Override
			public String getDataStoreName() {
				return "tbl_users.lname";
			}
		};
		name.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		name.setSortable(sortable);
		if (selectable) {
			name.setFieldUpdater(new FieldUpdater<Intervention, String>() {

				@Override
				public void update(int index, Intervention object, String value) {
					selectIntervention(object);
				}
			});
		}
		Header<String> nameFooter = new Header<String>(new ButtonCell()) {
			@Override
			public String getValue() {
				return "Add User";
			}
		};
		nameFooter.setUpdater(new ValueUpdater<String>() {
			
			@Override
			public void update(String value) {
				InterventionServiceAsync.Util.getInstance().addUser("Mark", "Donszelmann", new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						InterventionView.this.update();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						log.warn("Failed");
					}
				});
			}
		});
		table.addColumn(name, new TextHeader("Name"), nameFooter);

		// PtuID
		ClickableTextColumn<Intervention> ptu = new ClickableTextColumn<Intervention>() {
			@Override
			public String getValue(Intervention object) {
				return object.getPtuId();
			}

			@Override
			public String getDataStoreName() {
				return "tbl_devices.name";
			}
		};
		ptu.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		ptu.setSortable(sortable);
		if (selectable) {
			ptu.setFieldUpdater(new FieldUpdater<Intervention, String>() {

				@Override
				public void update(int index, Intervention object, String value) {
					selectIntervention(object);
				}
			});
		}
		Header<String> deviceFooter = new Header<String>(new ButtonCell()) {
			@Override
			public String getValue() {
				return "Add PTU";
			}
		};
		deviceFooter.setUpdater(new ValueUpdater<String>() {
			
			@Override
			public void update(String value) {
				InterventionServiceAsync.Util.getInstance().addDevice("PTU098982", new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						InterventionView.this.update();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						log.warn("Failed");
					}
				});
			}
		});
		table.addColumn(ptu, new TextHeader("PTU ID"), deviceFooter);


		// Description
		ClickableHtmlColumn<Intervention> description = new ClickableHtmlColumn<Intervention>() {
			@Override
			public String getValue(Intervention object) {
				return object.getDescription();
			}

			@Override
			public String getDataStoreName() {
				return "tbl_inspections.dscr";
			}
		};
		description.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		description.setSortable(true);
		if (selectable) {
			description
					.setFieldUpdater(new FieldUpdater<Intervention, String>() {

						@Override
						public void update(int index, Intervention object,
								String value) {
							selectIntervention(object);
						}
					});
		}
		table.addColumn(description, "Description");

		// Selection
		if (selectable) {
			final SingleSelectionModel<Intervention> selectionModel = new SingleSelectionModel<Intervention>();
			table.setSelectionModel(selectionModel);
			selectionModel
					.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

						@Override
						public void onSelectionChange(SelectionChangeEvent event) {
							Intervention m = selectionModel.getSelectedObject();
							log.info(m + " " + event.getSource());
						}
					});
		}

		return true;
	}

	private void selectIntervention(Intervention intervention) {
	}

	private void update() {
		table.redraw();
	}	
}
