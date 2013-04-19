package ch.cern.atlas.apvs.player;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.moxieapps.gwt.highcharts.client.ChartSubtitle;
import org.moxieapps.gwt.highcharts.client.ChartTitle;
import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.Exporting;
import org.moxieapps.gwt.highcharts.client.Global;
import org.moxieapps.gwt.highcharts.client.Highcharts;
import org.moxieapps.gwt.highcharts.client.Highcharts.Options;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Legend.Align;
import org.moxieapps.gwt.highcharts.client.Legend.Layout;
import org.moxieapps.gwt.highcharts.client.Legend.VerticalAlign;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.RangeSelector;
import org.moxieapps.gwt.highcharts.client.RangeSelector.Button;
import org.moxieapps.gwt.highcharts.client.RangeSelector.ButtonType;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.Series.Type;
import org.moxieapps.gwt.highcharts.client.StockChart;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.events.AxisSetExtremesEvent;
import org.moxieapps.gwt.highcharts.client.events.AxisSetExtremesEventHandler;
import org.moxieapps.gwt.highcharts.client.events.SeriesLegendItemClickEvent;
import org.moxieapps.gwt.highcharts.client.events.SeriesLegendItemClickEventHandler;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.SeriesPlotOptions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;

public class TimePlot extends DockPanel {
	
	private static final Logger logger = Logger.getLogger(TimePlot.class.getName());

	private static final String[] colors = { "#AA4643", "#89A54E", "#80699B",
		"#3D96AE", "#DB843D", "#92A8CD", "#A47D7C", "#B5CA92", "#4572A7" };

	private static final String dosePrefix = "Dose-";
	
	private StockChart chart;
	private Map<String, Series> seriesByName;
	private Map<String, Integer> colorIndexByName;
	private String url;
	private JsonpRequestBuilder jsonp;
	private String name;
	
	public TimePlot() {
		Highcharts.setOptions(new Options().setGlobal(new Global()
		.setUseUTC(true)));

		seriesByName = new HashMap<String, Series>();
		colorIndexByName = new HashMap<String, Integer>();

		jsonp = new JsonpRequestBuilder();
		jsonp.setTimeout(20000);
		
		url = "http://atlas.web.cern.ch/Atlas/TCOORD/CavCom/plot-data.php";
	}
	
	public void plot(final long start, final long end, String name) {
		
		this.name = name;
		
		jsonp.requestObject(url+"?start="+start+"&end="+end, new AsyncCallback<JsArray<JavaScriptObject>>() {

			@Override
			public void onSuccess(JsArray<JavaScriptObject> dataArray) {
				createPlot();
				
				// Nav Plot
				int nBins = 400;
				long binWidth = (end  - start) / nBins; 
				double[] hist = new double[nBins];

				for (int i=0; i<dataArray.length(); i++) {
					JavaScriptObject data = dataArray.get(i);
					String name = nativeGetName(data);
					if (name.startsWith(dosePrefix)) {
						continue;
					}
			        JsArray<JsArrayNumber> nativePoints = nativeGetData(data);
			        for (int j = 0; j < nativePoints.length(); j++) {
			        	JsArrayNumber nativePoint = nativePoints.get(j);
			        	long mid = (long)(nativePoint.get(0) + nativePoint.get(1)) / 2L;
			        	int bin = (int)((mid - start) / binWidth);
			        	GWT.log(bin+" "+start+" "+nativePoint.get(0)+" "+binWidth);
			        	hist[bin] = Math.max(hist[bin], nativePoint.get(2));
			        }
				}
				
				Point[] navPoints = new Point[nBins];
				for (int i=0; i<hist.length; i++) {
					navPoints[i] = new Point(i*binWidth + start, hist[i]);
				}
						
				// create special series for navigator as we cannot seem to use the standard series
				chart.addSeries(chart.createSeries().setName("NAV").setPoints(navPoints).setOption("visible", false).setOption("showInLegend", false));	
				chart.getNavigator().setBaseSeries(0);
				
				// add normal data
				setData(dataArray, start, end);

				add(chart, CENTER);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(""+caught);				
			}
		});

	}

	private void createPlot() {
		chart = new StockChart();

		chart.setSizeToMatchContainer();
		chart.setWidth100();	
		
		chart.setType(Type.AREA);
		chart.setOption("chart/zoomType", "x");
		chart.setAnimation(false);

		chart.setSeriesPlotOptions(new SeriesPlotOptions().setAnimation(false)
				.setMarker(new Marker().setEnabled(false))
				.setOption("dataGrouping/approximation", "high")
				.setOption("dataGrouping/enabled", false)
				.setSeriesLegendItemClickEventHandler(new SeriesLegendItemClickEventHandler() {
					
					@Override
					public boolean onClick(SeriesLegendItemClickEvent event) {
						Series series = seriesByName.get(dosePrefix+event.getSeriesName());
						if (series == null) return true;
						if (event.isVisible()) {
							series.hide();
						} else {
							series.show();
						}
						return true;
					}
				}));
		
		chart.setExporting(new Exporting().setEnabled(true));

		chart.setCredits(new Credits().setText("ATLAS Wireless Safety System")
				.setHref("http://cern.ch/ATWSS"));

		chart.setRangeSelector(new RangeSelector()
				.setButtons(
						new Button().setType(ButtonType.MINUTE).setCount(60)
								.setText("1h"),
						new Button().setType(ButtonType.MINUTE).setCount(180)
								.setText("3h"),
						new Button().setType(ButtonType.ALL).setText("All"))
				.setInputEnabled(false)
				.setSelected(2)
				.setEnabled(false));

		chart.getNavigator().getSeries()
				.setOption("dataGrouping/approximation", "high")
				.setOption("dataGrouping/smoothed", true)
				.setOption("dataGrouping/enabled", false);
		
		chart.getNavigator().setAdaptToUpdatedData(false);
		
		chart.setOption("scrollbar/liveRedraw", false);

		chart.setLegend(new Legend().setEnabled(true).setY(100)
				.setLayout(Layout.VERTICAL).setBorderWidth(2)
				.setAlign(Align.RIGHT).setVerticalAlign(VerticalAlign.TOP));

		chart.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
			
			private final DateTimeFormat dateFormat = DateTimeFormat.getFormat("EEEE, d MMMM yyyy HH:mm:ss");
			
			@Override
			public String format(ToolTipData data) {
				String s = "<b>" + dateFormat.format(new Date(data.getXAsLong()))+"</b>";

				for (int i=0; i<data.getPointsLength(); i++) {
					if (i % 2 > 0) {
						s += " " + data.getYAsDouble(i) + " \u00b5Sv";
					} else {
						s += " <br></br>" + data.getSeriesName(i) + ": " + data.getYAsDouble(i) + " \u00b5Sv/h";
					}
				}
			    return s;
			}
		}));
		
		chart.getXAxis()
				.setType(
						org.moxieapps.gwt.highcharts.client.Axis.Type.DATE_TIME)
				.setOption("ordinal", false)
				.setOption("minRange", 10 * 60 * 1000) // 10 Minutes
				.setOffset(0)
				.setAxisSetExtremesEventHandler(new AxisSetExtremesEventHandler() {
					
					@Override
					public boolean onSetExtremes(AxisSetExtremesEvent event) {
						getData(event.getMin().longValue(), event.getMax().longValue());
						return false;
					}
				});

		int titleHeight = 50;
		int rateChartHeight = 400;
		int doseChartHeight = 200;
		int chartSeparator = 50;
		int navigatorHeight = 100;
		int correction = 7;
		int chartHeight = titleHeight + rateChartHeight + chartSeparator + doseChartHeight + navigatorHeight - correction;
		
		chart.getYAxis(0)
				.setAxisTitleText("Rate [\u00b5Sv/h]")
				.setOffset(0)
				.setLineWidth(2)
				.setMin(0)
				.setOption("height", rateChartHeight);

		chart.getYAxis(1)
				.setAxisTitleText("Dose [\u00b5Sv]")
				.setOffset(0)
				.setLineWidth(2)
				.setMin(0)
				.setOption("height", doseChartHeight)
				.setOption("top", titleHeight + rateChartHeight + chartSeparator);
		
		chart.setHeight(chartHeight);		
	}
	
	private int colorIndex = 0;
	
	private Series get(String seriesName) {
		Series series = seriesByName.get(seriesName);
		if (series == null) {
			series = chart.createSeries().setName(seriesName);
			chart.addSeries(series, false, false);
			seriesByName.put(seriesName, series);
			
			int color;
			if (seriesName.startsWith(dosePrefix)) {
				color = colorIndexByName.get(seriesName.substring(dosePrefix.length()));
				
				series.setYAxis(1)
					.setOption("showInLegend", false)
					.setOption("tooltip/enabled", false)
					.setOption("tooltip/crosshairs", false)
					.setType(Type.LINE);
				
			} else {
				color = colorIndex;
				colorIndex++;
				colorIndex %= colors.length;
			}
			colorIndexByName.put(seriesName, color);
			series.setOption("color",  colors[color]);
			
			series.setOption("dataGrouping/enabled", false);
		}
		return series;
	}
	
	private void setData(JsArray<JavaScriptObject> dataArray, long start, long end) {
		
		chart.setTitle(new ChartTitle().setText("ATLAS Dosimeters"+(name != null ? " - "+name : "")),
				new ChartSubtitle().setText(unixToLocalTime(start, end)));
		
		for (int i=0; i<dataArray.length(); i++) {
			JavaScriptObject data = dataArray.get(i);
			String seriesName = nativeGetName(data);
			get(seriesName).setPoints(getPoints(data, !seriesName.startsWith(dosePrefix)));
		}
		
		chart.redraw();
	}
	
	private void getData(final long start, final long end) {
		chart.showLoading("Loading data from server...");
		GWT.log("data: "+start+" "+end+" "+(end - start));		
		GWT.log("binWidth: "+(end-start)/400);		
		
		jsonp.requestObject(url+"?start="+start+"&end="+end, new AsyncCallback<JsArray<JavaScriptObject>>() {
			
			@Override
			public void onSuccess(JsArray<JavaScriptObject> dataArray) {
				chart.hideLoading();
												
				setData(dataArray, start, end);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				chart.hideLoading();
				Window.alert(""+caught);				
			}
		});

	}
		
    private static Point[] getPoints(JavaScriptObject nativeSeries, boolean startEnd) {
        ArrayList<Point> convertedPoints = new ArrayList<Point>();
        JsArray<JsArrayNumber> nativePoints = nativeGetData(nativeSeries);
        for (int i = 0; i < nativePoints.length(); i++) {
            JsArrayNumber nativePoint = nativePoints.get(i);
            if (startEnd) {
            	convertedPoints.add(new Point(nativePoint.get(0), 0));
            	convertedPoints.add(new Point(nativePoint.get(0), nativePoint.get(2)));
            	convertedPoints.add(new Point(nativePoint.get(1), nativePoint.get(2)));
            	convertedPoints.add(new Point(nativePoint.get(1), 0));
            } else {
            	long mid = (long)(nativePoint.get(0) + nativePoint.get(1)) / 2L;
            	convertedPoints.add(new Point(mid, nativePoint.get(2)));
            }
        }
        return convertedPoints.toArray(new Point[convertedPoints.size()]);
    }

    private static native JsArray<JsArrayNumber> nativeGetData(JavaScriptObject series) /*-{
    	return series.data;
	}-*/;

	private static native String nativeGetName(JavaScriptObject series) /*-{
    	return series.name;
	}-*/;
	
	private static DateTimeFormat dateFormat1 = DateTimeFormat.getFormat("EEEE, d MMMM yyyy <b>HH:mm</b> - ");
	private static DateTimeFormat dateFormat2 = DateTimeFormat.getFormat("d MMMM yyyy");
	private static DateTimeFormat dateFormat3 = DateTimeFormat.getFormat("<b>HH:mm</b>");
	private static DateTimeFormat dateFormat4 = DateTimeFormat.getFormat("d MMMM yyyy <b>HH:mm</b>");

	private static String unixToLocalTime(long start, long end) {
		Date startDate = new Date(start);
		Date endDate = new Date(end);
		String result = dateFormat1.format(startDate);
		result += dateFormat2.format(startDate).equals(dateFormat2.format(endDate)) ? dateFormat3.format(endDate) : dateFormat4.format(endDate);	
		return result;
	}
}
