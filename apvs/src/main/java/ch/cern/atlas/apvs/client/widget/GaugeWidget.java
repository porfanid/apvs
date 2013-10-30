package ch.cern.atlas.apvs.client.widget;

import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.PlotBand;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.Series.Type;
import org.moxieapps.gwt.highcharts.client.labels.XAxisLabels;
import org.moxieapps.gwt.highcharts.client.labels.YAxisLabels;
import org.moxieapps.gwt.highcharts.client.plotOptions.BarPlotOptions;

public class GaugeWidget extends Chart {

	private PlotBand lowBand;
	private PlotBand highBand;
	private String bandColor;
	
	private Series series;

	public GaugeWidget() {		
		setAnimation(false);
		setPlotBorderWidth(2);
		setPlotBackgroundColor("#4D4DB8");
		setPlotBorderColor("#D8D8D8");
		setPlotShadow(true);
		setSpacingBottom(43);
		setWidth(250);

		setCredits(new Credits().setEnabled(false));

		getXAxis().setLabels(new XAxisLabels().setEnabled(false))
				.setTickLength(0);

		setTitle(null);
		setLegend(new Legend().setEnabled(false));

		getYAxis().setAxisTitleText(null).setLabels(new YAxisLabels().setY(20))
				.setTickWidth(1).setTickLength(8).setMinorTickWidth(1)
				.setMinorTickLength(5).setMinorGridLineWidth(0);

		bandColor = "#B84D4D";
		lowBand = getYAxis().createPlotBand().setColor(bandColor);
		highBand = getYAxis().createPlotBand().setColor(bandColor);

		series = createSeries();
		series.setType(Type.BAR);

		addSeries(series);

		setBarPlotOptions(new BarPlotOptions().setAnimation(false)
				.setBorderColor("#FFFFFF").setBorderRadius(2).setBorderWidth(2)
				.setColor("#000000").setPointWidth(5));
	}

	public void setValue(Number value, Number downThreshold, Number upThreshold) {
		double low = downThreshold.doubleValue();
		double high = upThreshold.doubleValue();
		double val = value.doubleValue();
		
		double percent = (high - low) / 60;
		double highPercent = 20;
		double lowPercent = 20;
		double tick = 5;
		if (val > high) {
			percent *= 2;
			tick *= 2;
			lowPercent = 10;
			highPercent = 60;
		} else if (val < low) {
			percent *= 2;
			tick *= 2;
			lowPercent = 60;
			highPercent = 10;			
		}
		
		double min = low - lowPercent * percent;
		double max = high + highPercent * percent;
		double tickInterval = (max - min) / tick;
		double minorTickInterval = tickInterval / 2;

		getYAxis().setMin(min).setMax(max).setTickInterval(tickInterval)
				.setMinorTickInterval(minorTickInterval);

		lowBand.setFrom(min).setTo(low);
		highBand.setFrom(upThreshold).setTo(high);
		
		series.setPoints(new Number[] {value}, true);
	}
}
