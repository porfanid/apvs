package ch.cern.atlas.apvs.client.traceplot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;
import com.hydro4ge.raphaelgwt.client.BBox;
import com.hydro4ge.raphaelgwt.client.PathBuilder;
import com.hydro4ge.raphaelgwt.client.RaphaelObject;
import com.rcharts.client.AxisLabelHelper;
import com.rcharts.client.Chart;
import com.rcharts.client.Point;
import com.rcharts.client.Position;
import com.rcharts.client.RDockPanel;
import com.rcharts.client.RaphaelFactory;
import com.rcharts.client.category.Axis;
import com.rcharts.client.category.AxisFactory;
import com.rcharts.client.category.CategoryHoverHandler;
import com.rcharts.client.category.LegendFactory;

public class TracePlot extends Chart {

	private DataSource<Long, Double> dataTable;
	private Map<String, List<TraceData>> traceDataTable;

	public static double POINT_RADIUS = 3;

//	private List<CategoryHoverHandler> handlers = new ArrayList<CategoryHoverHandler>();
//	private List<RaphaelObject> bubbles = new ArrayList<RaphaelObject>();
	
	public static class TraceData {
		private DataPoint<Long, Double> point;
		private RaphaelObject rPoint;
		
		public TraceData(DataPoint<Long, Double> point, RaphaelObject rPoint){
			this.point = point;
			this.rPoint = rPoint;
		}
		
		public DataPoint<Long, Double> getPoint(){
			return point;
		}
		
		public RaphaelObject getRaphaelPoint(){
			return rPoint;
		}
	}

	public TracePlot(int width, int height) {
		super(width, height);
	}

	@Override
	protected void draw() {
		chartPanel = new RDockPanel();
		setTitle(chartPanel);
		LegendFactory legFactory = new LegendFactory();
		chartPanel.add(legFactory.getLegendPanel(), Position.NORTH);

		traceDataTable = new HashMap<String, List<TraceData>>();

		Set plotSet = new Set();
		Set chartSet = new Set();
		
		AxisFactory axisFactory = new AxisFactory();
		Axis xAxis = axisFactory.getXAxis();
		Axis yAxis = axisFactory.getYAxis();
		
		// add axes
		List<Shape> list = xAxis.getAxisList();
		for (Shape s : list) {
			System.err.println("A: "+s.getBBox().x()+" "+s.getBBox().y()+" "+s.getBBox().width()+" "+s.getBBox().height());
//			chartSet.push(new Rect(s.getBBox().x(), s.getBBox().y(), s.getBBox().width(), s.getBBox().height()));
			chartSet.push(s);
		}
		list = yAxis.getAxisList();
		for (Shape s : list) {
			chartSet.push(s);
		}

		// FIXME, remove... Add origin to debug
		Point originPoint = new Point(xAxis.getTickPoints().get(0).getX(),
				yAxis.getTickPoints().get(0).getY());
		Shape org = new Circle(originPoint.getX(), originPoint.getY(), 10);
		plotSet.push(org);
		chartSet.push(org);
		
		// FIXME both call getxMax and getyMax...
		double axisWidth = AxisLabelHelper.getXAxisLength();
		System.err.println("400 = "+AxisLabelHelper.getXAxisLength());
		double axisHeight = AxisLabelHelper.getYAxisLength();
		System.err.println("280 = "+AxisLabelHelper.getYAxisLength());

		// FIXME, doubt this should be double 
		long xMax = Long.parseLong(xAxis.getTickLabel().get(
				xAxis.getTickLabel().size() - 1));
		long xMin = Long.parseLong(xAxis.getTickLabel().get(0));
		double yMax = Double.parseDouble(yAxis.getTickLabel().get(
				yAxis.getTickLabel().size() - 1));
		double yMin = Double.parseDouble(yAxis.getTickLabel().get(0));
		double xRatio = axisWidth / (xMax - xMin); // axisWidth / (xMax - xMin);// dataTable.getxMax();
		double yRatio = axisHeight / (yMax - yMin); // axisHeight / (yMax - yMin);// dataTable.getyMax();
		System.err.println("X "+xMax+" "+xMin+" "+xAxis.getTickLabel().size());
		System.err.println("Y "+yMax+" "+yMin+" "+yAxis.getTickLabel().size());
		System.err.println("R "+xRatio+" "+yRatio);
		
		Map<String, String> colorMap = RaphaelFactory.getColorMap();
		Collection<String> seriesNames = dataTable.getSeriesNames();
		Iterator<String> seriesIt = seriesNames.iterator();
		while (seriesIt.hasNext()) {
			List<TraceData> traceData = new ArrayList<TraceData>();
			String series = seriesIt.next();
			List<DataPoint<Long, Double>> points = dataTable.get(series);
			String color = colorMap.get(series);
			if (points.size() != 0) {
				PathBuilder linePath = new PathBuilder();
				boolean move = true;
				for (DataPoint<Long, Double> point : points) {
					double x = originPoint.getX() + (point.getX() * xRatio);
					double y = originPoint.getY() - (point.getY() * yRatio);
					Circle c = new Circle(x, y, POINT_RADIUS);
					c.attr("fill", color);
					if (move) {
						linePath.M(x, y);
						move = false;
					} else {
						linePath.L(x, y);
					}
					plotSet.push(c);
					chartSet.push(c);
					traceData.add(new TraceData(point, c));
				}
				System.err.println(linePath);
				Path line = new Path(linePath);
				line.attr("stroke", color);
				line.attr("stroke-width", 2);
				plotSet.push(line);
				chartSet.push(line);
			}
			traceDataTable.put(series, traceData);
		}
	
		DataPoint<Long, Double> origin = new DataPoint<Long, Double>(0L, 0.0);
		
		BBox xBox = xAxis.getAxisLine().getBBox();
		double xDx = origin.getX() - xBox.x();
		double xDy = origin.getY() - xBox.y() - xBox.height();
		System.err.println("xD "+xDx+" "+xDy);
		xAxis.get().translate(xDx, xDy);

		BBox yBox = yAxis.getAxisLine().getBBox();
		double yDx = origin.getX() - yBox.x();// - yBox.width();
		double yDy = (origin.getY() - yBox.height()) - yBox.y();
		System.err.println("yD "+yDx+" "+yDy);
		yAxis.get().translate(yDx, yDy);		
				
		BBox cBox = plotSet.getBBox();
		double cDx = origin.getX() - cBox.x();
		double cDy = (origin.getY() - cBox.height()) - cBox.y() - 140;
// FIXME, should this not be from Axis ?
//		xMin = dataTable.getMinX();
//		yMin = dataTable.getMinY();
		// FIXME again doubled by two ?
//		if(xMin < 0){
//			double ratio = axisWidth/(xMax*2);
//			double diff = ratio * (xMax+xMin);
//			cDx = cDx + diff;			
//		}
//		if(yMin < 0){
//			double ratio = axisHeight/(yMax*2);
//			double diff = ratio * (yMax+yMin);
//			cDy = cDy - diff;			
//		}

		System.err.println("cD "+cDx+" "+cDy);
		plotSet.translate(xDx, yDy);
		
		// resize to view port
		boolean rescale = true;
		if (rescale) {
			BBox plotBox = chartSet.getBBox();
			double dx = (chartPanel.getWidth() - 15) / plotBox.width();
			double dy = (chartPanel.getHeight() - 15) / plotBox.height();
			chartSet.scale(dx, dy, plotBox.x(), plotBox.y());
		}
		chartPanel.add(chartSet, Position.SOUTH);

		setHandler(legFactory.getLegendMarkMap());
	}

	private void setHandler(Map<String, RaphaelObject> legMarkMap) {
		Collection<String> seriesNames = traceDataTable.keySet();
		for (String series : seriesNames) {
			List<TraceData> traceDataList = traceDataTable.get(series);
			for (TraceData traceData : traceDataList) {
				CategoryHoverHandler handler = GWT
						.create(CategoryHoverHandler.class);
				DataPoint<Long, Double> point = traceData.getPoint();
				RaphaelObject legend = legMarkMap.get(series);
				RaphaelObject popPoint = traceData.getRaphaelPoint();
				// FIXME...
				Position popupPosition = Position.NORTH;
				if (point.getY() < 0) {
					popupPosition = Position.SOUTH;
				}
				handler.initXY(series, point.getY(), point.getX(), legend,
						popPoint, popupPosition);
				popPoint.addMouseOverHandler(handler);
				((Widget) popPoint).addDomHandler(handler,
						MouseOutEvent.getType());
//				handlers.add(handler);
//				bubbles.add(handler.getBubble().get());
			}
		}
	}

	@Override
	public void clear() {		
		chartPanel.clear();
	}

	public void update() {
		draw();
	}

	@Override
	protected void initRaphaelFactory() {
		// FIXME needed for colorMap in LegendFactory...
		RaphaelFactory.setSeries(dataTable.getSeriesNames());
	}

	// FIXME should be List
	@Override
	public java.util.Set<String> getXAxisLabels() {
		java.util.Set<String> set = new LinkedHashSet<String>();
		for (Iterator<Long> i = LabelMaker.getValueAxisLabels(dataTable.getMaxX(), dataTable.getMinX(), 0, 5).iterator();i.hasNext(); ) {
			set.add(Long.toString(i.next()));
		}
		return set;
	}
	
	NumberFormat yFormat = NumberFormat.getFormat("0.##");

	// FIXME should be List
	@Override
	public java.util.Set<String> getYAxisLabels() {
		java.util.Set<String> set = new LinkedHashSet<String>();
		for (Iterator<Double> i = LabelMaker.getValueAxisLabels(dataTable.getMaxY(), dataTable.getMinY(), 0, 5).iterator();i.hasNext(); ) {
			set.add(yFormat.format(i.next()));
		}
		return set;
	}

	@Override
	public boolean isXValueAxis() {
		return true;
	}

	@Override
	public boolean isYValueAxis() {
		return true;
	}

	public void setDataTable(DataSource<Long, Double> dataTable) {
		this.dataTable = dataTable;
	}
	
	public DataSource<Long, Double> getDataTable() {
		return dataTable;
	}
}
