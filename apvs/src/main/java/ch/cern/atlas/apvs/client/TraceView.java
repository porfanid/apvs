package ch.cern.atlas.apvs.client;

import com.rcharts.client.category.CategoryDataTable;
import com.rcharts.client.category.line.LineChart;
import com.google.gwt.user.client.ui.SimplePanel;

public class TraceView extends SimplePanel {

	public TraceView() {
		CategoryDataTable<Double> dataTable = new CategoryDataTable<Double>();
		
		double[] y = {20, 10, 20, 25, 27, 30};
		
		String[] x = {"1", "2", "3", "4", "5", "6"};
		String[] name = {"O2"};
		
		for (int i=0; i < x.length; i++) {
			dataTable.add(y[i], x[i], name[0]);
		}
		
		LineChart chart = new LineChart(600, 400);
		chart.setDataTable(dataTable);
		
		add(chart);
	}
}
