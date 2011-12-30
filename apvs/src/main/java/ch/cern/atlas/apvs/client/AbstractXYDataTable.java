package ch.cern.atlas.apvs.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rcharts.client.Point;
import com.rcharts.client.xychart.XYDataTable;

public abstract class AbstractXYDataTable<E extends Comparable <? super E>> extends XYDataTable<E> {
			
	@Override
	public void add(Double xValue, Double yValue, String series) {
		throw new RuntimeException("AbstractXYDataTable.add() not implemted");
	}
	
	@Override
	public void setPointMap(Map<String, List<Point>> pointMap) {
		throw new RuntimeException("AbstractXYDataTable.setPointMap() not implemted");
	}
		
	@Override
	public abstract Set<String> getSeriesNames();

	@Override
	public abstract List<Point> get(String series);
	
	@Override
	public abstract Map<String, List<Point>> getPointMap();
		
	@Override
	public abstract double getxMax();
	
	@Override
	public abstract double getxMin();
	
	@Override
	public abstract double getyMax();
	
	@Override
	public abstract double getyMin();
	
	@Override
	public abstract String toString();
}
