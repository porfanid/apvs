package ch.cern.atlas.apvs.client.ui;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.traceplot.DataPoint;
import ch.cern.atlas.apvs.client.traceplot.DataSource;
import ch.cern.atlas.apvs.client.traceplot.TracePlot;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;

public class TraceView extends SimplePanel {

	private TracePlot plot;
	private Ptu ptu;
	private String name = "CO<sub>2</sub> Sensor";

	public TraceView(ClientFactory clientFactory) {
		DataSource<Long, Double> dataTable = new DataSource<Long, Double>() {
		
			@Override
			public Set<String> getSeriesNames() {
				System.err.println("getSeriesNames()");
				return new HashSet<String>(Arrays.asList(new String[] {name}));
			}

			@Override
			public List<DataPoint<Long, Double>> get(final String series) {
				// FIXME series
				System.err.println("get("+series+")");
				
				return new AbstractList<DataPoint<Long, Double>>() {

					@Override
					public DataPoint<Long, Double> get(int index) {
						if (ptu == null) return null;

						Measurement<Double> m0 = ptu.getMeasurement(series, 0);
						Measurement<Double> m = ptu.getMeasurement(series, index);
//						System.err.println(index+" "+m.getDate().getTime()+" "+m.getValue());
						return new DataPoint<Long, Double>((m.getDate().getTime()-m0.getDate().getTime())/1000, m.getValue());
					}

					@Override
					public int size() {
						if (ptu == null) return 0;

						return ptu.getNumberOfMeasurements(series);
					}
					
				};
			}

			@Override
			public Long getMaxX() {
				// FIXME.. all series
				// FIXME should be null
				if (ptu == null) return 0L;
				
				Measurement<Double> m0 = ptu.getMeasurement(name, 0);
				Measurement<Double> m = ptu.getMeasurement(name, ptu.getNumberOfMeasurements(name)-1);
				long max = (m.getDate().getTime() - m0.getDate().getTime())/1000;
				System.err.println("getMaxX() "+max+" FIXME");
				return max;
			}

			@Override
			public Long getMinX() {
				// FIXME should be null
				if (ptu == null) return 0L;
				Measurement<Double> m = ptu.getMeasurement(name, 0);
				long min = 0; // m.getDate().getTime()/1000;
				System.err.println("getMinX() "+min+" FIXME");
				return min;
			}

			@Override
			public Double getMaxY() {
				// FIXME should be null
				if (ptu == null) return 0.0;
				
				double max = Double.MIN_VALUE;
				for (int i=0; i<ptu.getNumberOfMeasurements(name); i++) {
					max = Math.max(max, ptu.getMeasurement(name, i).getValue());
				}
				System.err.println("getMaxY() "+max);
				return max;
			}

			@Override
			public Double getMinY() {
				// FIXME should be null
				if (ptu == null) return 0.0;
				
				double min = Double.MAX_VALUE;
				for (int i=0; i<ptu.getNumberOfMeasurements(name); i++) {
					min = Math.min(min, ptu.getMeasurement(name, i).getValue());
				}
				System.err.println("getMinY() "+min);
				return min;
			}

			@Override
			public String toString() {
				return "XYDataTable";
			}

		};
				
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
	}
}
