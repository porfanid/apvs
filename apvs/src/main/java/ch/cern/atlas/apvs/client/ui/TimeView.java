package ch.cern.atlas.apvs.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class TimeView extends AbstractTimeView {

	private HandlerRegistration measurementHandler;
	private final RemoteEventBus localEventBus;

	private Integer ptuId = null;
	private String measurementName = null;
	private EventBus cmdBus;

	public TimeView(final ClientFactory clientFactory,
			RemoteEventBus workerEventBus, int height, boolean export,
			Arguments args) {
		this.clientFactory = clientFactory;
		this.localEventBus = workerEventBus;
		this.height = height;
		this.export = export;

		SelectPtuEvent.subscribe(localEventBus, new SelectPtuEvent.Handler() {

			@Override
			public void onPtuSelected(final SelectPtuEvent event) {
				if (!localEventBus.getUUID().equals(event.getEventBusUUID()))
					return;
				ptuId = event.getPtuId();
				updateChart();
			}
		});
		
		cmdBus = NamedEventBus.get(args.getArg(0));
		measurementName = args.getArg(1);
		
		SelectMeasurementEvent.register(cmdBus, MeasurementView.source, new SelectMeasurementEvent.Handler() {
			
			@Override
			public void onSelection(SelectMeasurementEvent event) {
				measurementName = event.getName();
				updateChart();				
			}
		});
		
	}

	private void updateChart() {
		pointsById = new HashMap<Integer, Integer>();
		seriesById = new HashMap<Integer, Series>();
		colorsById = new HashMap<Integer, String>();

		unsubscribe();
		removeChart();

		if (measurementName.equals("")) {
			return;
		}

		if ((ptuId == null) || (ptuId == 0)) {
			// Subscribe to all PTUs
			final long t0 = System.currentTimeMillis();
			clientFactory
					.getPtuService()
					.getMeasurements(
							measurementName,
							new AsyncCallback<Map<Integer, List<Measurement<Double>>>>() {

								@Override
								public void onSuccess(
										Map<Integer, List<Measurement<Double>>> measurements) {
									if (measurements == null) {
										return;
									}

									System.err.println("Measurement Map retrieval took "
											+ (System.currentTimeMillis() - t0)
											+ " ms for " + measurements.size());

									String unit = "";

									if (measurements.keySet().size() > 0) {
										List<Measurement<Double>> m = measurements
												.get(measurements.keySet()
														.iterator().next());
										if ((m != null) && (m.size() > 0)) {
											unit = m.get(0).getUnit();
										}
									}

									createChart(measurementName, unit);

									int k = 0;
									for (Iterator<Integer> i = measurements
											.keySet().iterator(); i.hasNext();) {
										int ptuId = i.next();

										Series series = chart.createSeries()
												.setName("" + ptuId);
										pointsById.put(ptuId, 0);
										seriesById.put(ptuId, series);
										colorsById.put(ptuId, color[k]);

										addHistory(ptuId, series,
												measurements.get(ptuId));

										chart.addSeries(series, true, false);
										k++;
									}

									add(chart);

									subscribe(null, measurementName);
								}

								@Override
								public void onFailure(Throwable caught) {
									System.err
											.println("Cannot retrieve Measurements "
													+ caught);
								}
							});
		} else {
			// subscribe to single PTU
			final long t0 = System.currentTimeMillis();
			clientFactory.getPtuService().getMeasurements(ptuId,
					measurementName,
					new AsyncCallback<List<Measurement<Double>>>() {

						@Override
						public void onSuccess(
								List<Measurement<Double>> measurements) {
							if (measurements == null) {
								return;
							}

							System.err.println("Measurement retrieval took "
									+ (System.currentTimeMillis() - t0)
									+ " ms for " + measurements.size());

							String unit = measurements.size() > 0 ? measurements
									.get(0).getUnit() : "";

							createChart(measurementName + " (" + ptuId + ")",
									unit);

							Series series = chart.createSeries().setName(
									"" + ptuId);
							pointsById.put(ptuId, 0);
							seriesById.put(ptuId, series);
							colorsById.put(ptuId, color[0]);

							addHistory(ptuId, series, measurements);

							chart.addSeries(series, true, false);

							add(chart);

							subscribe(ptuId, measurementName);
						}

						@Override
						public void onFailure(Throwable caught) {
							System.err.println("Cannot retrieve Measurements "
									+ caught);
						}
					});
		}
	}

	private void addHistory(Integer ptuId, Series series,
			List<Measurement<Double>> measurements) {
		if (measurements == null)
			return;

		Number[][] data = new Number[measurements.size()][2];

		for (int i = 0; i < data.length; i++) {
			Measurement<Double> m = measurements.get(i);

			data[i][0] = m.getDate().getTime();
			data[i][1] = m.getValue();
		}

		series.setPoints(data, false);
		pointsById.put(ptuId, data.length);
	}

	private void unsubscribe() {
		if (measurementHandler != null) {
			measurementHandler.removeHandler();
			measurementHandler = null;
		}
	}

	private void subscribe(final Integer ptuId, final String name) {
		unsubscribe();

		measurementHandler = MeasurementChangedEvent.register(
				clientFactory.getEventBus(),
				new MeasurementChangedEvent.Handler() {

					@Override
					public void onMeasurementChanged(
							MeasurementChangedEvent event) {
						Measurement<Double> m = event.getMeasurement();

						if ((ptuId != null) && (m.getPtuId() != ptuId)) {
							return;
						}

						if (m.getName().equals(name)) {
							System.err.println("New meas " + m);
							Series series = seriesById.get(m.getPtuId());
							if (series != null) {
								Integer numberOfPoints = pointsById.get(ptuId);
								if (numberOfPoints == null)
									numberOfPoints = 0;
								boolean shift = numberOfPoints >= pointLimit;
								if (!shift) {
									pointsById.put(ptuId, numberOfPoints + 1);
								}
								chart.setLinePlotOptions(new LinePlotOptions()
										.setMarker(new Marker()
												.setEnabled(!shift)));

								series.addPoint(m.getDate().getTime(),
										m.getValue(), true, shift, true);
							}
						}
					}
				});
	}
}
