package ch.cern.atlas.apvs.client.ui;

import java.util.Date;

import org.moxieapps.gwt.highcharts.client.Axis;
import org.moxieapps.gwt.highcharts.client.AxisTitle;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.ChartTitle;
import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;

public class TraceView2 extends SimplePanel {

	private Ptu ptu;
	private String name = "CO<sub>2</sub> Sensor";
	private Chart chart;
	private Series series;

	public TraceView2(final ClientFactory clientFactory) {
						
		final long t0 = System.currentTimeMillis();
		clientFactory.getPtuService().getPtu(39400, new AsyncCallback<Ptu>() {
			
			@Override
			public void onSuccess(Ptu result) {
				System.err.println("PTU retrieval took "+(System.currentTimeMillis() - t0)+" ms");
				ptu = result;

				if (ptu == null) return;
			
			if (chart != null) {
				remove(chart);
			}
				
			chart = new Chart()
			   	.setType(Series.Type.LINE)
			   	.setChartTitle(new ChartTitle().setText(name))
			   	.setMarginRight(10)
			   	.setLinePlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)))
			   	.setLegend(new Legend().setEnabled(false))
			   	.setCredits(new Credits().setEnabled(false))
			   	.setToolTip(new ToolTip()
			   	    .setCrosshairs(true, true)
			   		.setFormatter(new ToolTipFormatter() {
			   			@Override
			   			public String format(ToolTipData toolTipData) {
			   				return "<b>" + toolTipData.getSeriesName() + "</b><br/>" +
								DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss")
									.format(new Date(toolTipData.getXAsLong())) + "<br/>" +
								NumberFormat.getFormat("0.00")
									.format(toolTipData.getYAsDouble());
			   			}
			   		})
			   	);
			
			chart.getXAxis()
			   	.setType(Axis.Type.DATE_TIME);
			
			Measurement<Double> m0 = ptu.getMeasurement(name, 0);
			String unit = m0 != null ?  m0.getUnit() : "";
			
			chart.getYAxis()
				.setAllowDecimals(true)
				.setAxisTitle(new AxisTitle().setText(unit));
			
		    series = chart.createSeries()
		    		.setName(name);
					
			add(chart);
				
				for(int i=0; i < ptu.getNumberOfMeasurements(name); i++) {
					Measurement<Double> m = ptu.getMeasurement(name, i);
					series.addPoint(m.getDate().getTime(), m.getValue());					
				}
				
				chart.addSeries(series, true, false);
				
				MeasurementChangedEvent.register(clientFactory.getEventBus(), new MeasurementChangedEvent.Handler() {
					
					@Override
					public void onMeasurementChanged(MeasurementChangedEvent event) {
						Measurement<Double> m = event.getMeasurement();
						
						Integer ptuId = m.getPtuId();
						if ((ptu != null) && (ptu.getPtuId() != ptuId)) return;

						if (m.getName().equals(name)) {
							System.err.println("New meas "+m);
							series.addPoint(m.getDate().getTime(), m.getValue(), true, true, true);
						}
					}
				});
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.err.println("Cannot retrieve PTU "+caught);
			}
		});
	}
}
