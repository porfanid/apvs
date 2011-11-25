package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.ListDataProvider;

public class PtuView extends SimplePanel {

	private ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	private CellTable<String> table = new CellTable<String>();
	private ListHandler<String> columnSortHandler;
	private PtuServiceAsync ptuService = PtuServiceAsync.Util.getInstance();
//	private Map<Integer, Dosimeter> dosimeters = new HashMap<Integer, Dosimeter>();
	
	Map<Integer, Ptu> ptus = new HashMap<Integer, Ptu>();
	Map<String, String> units = new HashMap<String, String>();
	
	public PtuView() {
		Ptu ptu;
		ptu = new Ptu(2030);
		ptu.add(new Measurement<Double>("Temperature", 22.4, "&deg;C"));
		ptu.add(new Measurement<Double>("CO<sub>2</sub>", 14.0, "ppm"));
		ptu.add(new Measurement<Double>("O<sub>2</sub>", 80.4, "ppm"));
		ptu.add(new Measurement<Double>("Rate", 1.2, "&micro;Sv/h"));
		ptus.put(ptu.getPtuId(), ptu);
		
		ptu = new Ptu(4378);
		ptu.add(new Measurement<Double>("Temperature", 26.8, "&deg;C"));
		ptu.add(new Measurement<Double>("CO<sub>2</sub>", 17.6, "ppm"));
		ptu.add(new Measurement<Double>("Rate", 1.3, "&micro;Sv/h"));
		ptus.put(ptu.getPtuId(), ptu);
				
		add(table);

		// name
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
		// name
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

		// FIXME
		
		for (Iterator<Integer> i = ptus.keySet().iterator(); i.hasNext(); ) {
			final Integer ptuId = i.next();
			TextColumn<String> column = new TextColumn<String>() {
				@Override
				public String getValue(String object) {
					Measurement<Double> m = ptus.get(ptuId).getMeasurement(object);
					return m != null ? m.getValue().toString() : "";
				}
			};
			column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			table.addColumn(column, ptuId.toString());
		}
		
		setNames(dataProvider.getList());		
		
//		getDosimeterMap();
	}
	
	private void setNames(List<String> names) {
		names.clear();
		Set<String> s = new HashSet<String>();
		for (Iterator<Ptu> i = ptus.values().iterator(); i.hasNext(); ) {
			Ptu ptu = i.next();
			List<String> measurements = ptu.getMeasurementNames();
			s.addAll(measurements);
			
			for (Iterator<String> j = measurements.iterator(); i.hasNext(); ) {
//				ptu.getMeasurement(i.next())
			}
		}
		names.addAll(s);
	}
	
	/*
	private void getDosimeterMap() {
		dosimeterService.getDosimeterMap(dosimeters.hashCode(),
				new AsyncCallback<Map<Integer, Dosimeter>>() {

					@Override
					public void onSuccess(Map<Integer, Dosimeter> result) {
						if (result == null) {
							System.err
									.println("FIXME onSuccess null in dosimeterView");
							return;
						}
						dosimeters = result;

						List<Dosimeter> list = dataProvider.getList();
						list.clear();

						for (Iterator<Dosimeter> i = result.values().iterator(); i
								.hasNext();) {
							list.add(i.next());
						}

						// Resort the table
						ColumnSortEvent.fire(table, table.getColumnSortList());
						table.redraw();
						
						getDosimeterMap();
					}

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Could not retrieve dosimeter map");

						getDosimeterMap();
					}
				});

	}
*/
}
