package ch.cern.atlas.apvs.client.traceplot;

import java.util.List;
import java.util.Set;


public interface DataSource<X, Y> {

	Set<String> getSeriesNames();

	List<DataPoint<X, Y>> get(String series);

	X getMaxX();

	X getMinX();

	Y getMaxY();

	Y getMinY();

}
