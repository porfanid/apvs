package ch.cern.atlas.apvs.client.ui;

import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Series;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.domain.Ptu;

import com.google.gwt.user.client.ui.SimplePanel;

public class TraceView2 extends SimplePanel {

	private Ptu ptu;
	private String name = "CO<sub>2</sub> Sensor";

	public TraceView2(ClientFactory clientFactory) {
		
		Chart chart = new Chart()
		   .setType(Series.Type.SPLINE)
		   .setChartTitleText("Lawn Tunnels")
		   .setMarginRight(10);
		
		Series series = chart.createSeries()
				   .setName("Moles per Yard")
				   .setPoints(new Number[] { 163, 203, 276, 408, 547, 729, 628 });
				chart.addSeries(series);
				
		add(chart);
		
		/*
		plot = new TracePlot(400, 200);
		plot.setDataTable(dataTable);
		
		add(plot);
		
		final long t0 = System.currentTimeMillis();
		clientFactory.getPtuService().getPtu(39400, new AsyncCallback<Ptu>() {
			
			@Override
			public void onSuccess(Ptu result) {
				System.err.println("PTU retrieval took "+(System.currentTimeMillis() - t0)+" ms");
				ptu = result;

				plot.clear();
				plot.update();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Cannot retrieve PTU "+caught);
			}
		});
		*/
	}
}
