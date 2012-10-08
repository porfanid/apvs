package ch.cern.atlas.apvs.client.ui;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.service.InterventionServiceAsync;
import ch.cern.atlas.apvs.client.service.SortOrder;
import ch.cern.atlas.apvs.client.widget.ClickableHtmlColumn;
import ch.cern.atlas.apvs.client.widget.ClickableTextColumn;
import ch.cern.atlas.apvs.ptu.shared.PtuClientConstants;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.DataGrid;
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

	public InterventionView() {
	}
	
	@Override
	public boolean configure(Element element, ClientFactory clientFactory, Arguments args) {


		String height = args.getArg(0);

		table.setSize("100%", height);
		table.setEmptyTableWidget(new Label("No Interventions"));
		table.setVisibleRange(0, 10);
		
		add(table);

		AsyncDataProvider<Intervention> dataProvider = new AsyncDataProvider<Intervention>() {

			@SuppressWarnings("unchecked")
			@Override
			protected void onRangeChanged(HasData<Intervention> display) {
				InterventionServiceAsync.Util.getInstance().getRowCount(new AsyncCallback<Integer>() {
					
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
				for (int i=0; i<sortList.size(); i++) {
					ColumnSortInfo info = sortList.get(i);
					// FIXME #88 remove cast
					order[i] = new SortOrder(((ClickableTextColumn<Intervention>)info.getColumn()).getDataStoreName(), info.isAscending());
				}
				
				if (order.length == 0) {
					order = new SortOrder[1];
					order[0] = new SortOrder("tbl_inspections.endtime", true);
				} 	
				
				InterventionServiceAsync.Util.getInstance().getTableData(range, order, new AsyncCallback<List<Intervention>>() {
					
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
		table.addColumn(name, "Name");
		// columnSortHandler.setComparator(name, new Comparator<Intervention>()
		// {
		// public int compare(Intervention o1, Intervention o2) {
		// if (o1 == o2) {
		// return 0;
		// }
		//
		// if (o1 != null) {
		// return (o2 != null) ? o1.getName().compareTo(o2.getName())
		// : 1;
		// }
		// return -1;
		// }
		// });

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
		table.addColumn(ptu, "PTU ID");
		// columnSortHandler.setComparator(ptu, new Comparator<Intervention>() {
		// public int compare(Intervention o1, Intervention o2) {
		// if (o1 == o2) {
		// return 0;
		// }
		//
		// if (o1 != null) {
		// return (o2 != null) ? o1.getPtuId()
		// .compareTo(o2.getPtuId()) : 1;
		// }
		// return -1;
		// }
		// });

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
		table.addColumn(startTime, "Start Time");
		// columnSortHandler.setComparator(startTime,
		// new Comparator<Intervention>() {
		// public int compare(Intervention o1, Intervention o2) {
		// if (o1 == o2) {
		// return 0;
		// }
		//
		// if (o1 != null) {
		// return (o2 != null) ? o1.getStartTime().compareTo(
		// o2.getStartTime()) : 1;
		// }
		// return -1;
		// }
		// });
		table.getColumnSortList().push(startTime);

		// endTime
		ClickableTextColumn<Intervention> endTime = new ClickableTextColumn<Intervention>() {
			@Override
			public String getValue(Intervention object) {
				return object.getEndTime() != null ? PtuClientConstants.dateFormat
						.format(object.getEndTime()) : "null";
			}
			
			@Override
			public String getDataStoreName() {
				return "tbl_inspections.starttime";
			}
		};
		endTime.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		endTime.setSortable(sortable);
		if (selectable) {
			endTime.setFieldUpdater(new FieldUpdater<Intervention, String>() {

				@Override
				public void update(int index, Intervention object, String value) {
					selectIntervention(object);
				}
			});
		}
		table.addColumn(endTime, "End Time");
//		columnSortHandler.setComparator(endTime,
//				new Comparator<Intervention>() {
//					public int compare(Intervention o1, Intervention o2) {
//						if (o1 == o2) {
//							return 0;
//						}
//
//						if (o1 != null) {
//							return (o2 != null) ? o1.getEndTime().compareTo(
//									o2.getEndTime()) : 1;
//						}
//						return -1;
//					}
//				});
		table.getColumnSortList().push(endTime);

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

//	private void update() {
//		// Re-sort the table
//		if (sortable) {
//			ColumnSortEvent.fire(table, table.getColumnSortList());
//		}
//		table.redraw();
//
//		if (selectable) {
//			Intervention selection = selectionModel.getSelectedObject();
//
//			if ((selection == null) && (dataProvider.getList().size() > 0)) {
//				selection = dataProvider.getList().get(0);
//
//				selectIntervention(selection);
//			}
//
//			// re-set the selection as the async update may have changed the
//			// rendering
//			if (selection != null) {
//				selectionModel.setSelected(selection, true);
//			}
//		}
//	}
}
