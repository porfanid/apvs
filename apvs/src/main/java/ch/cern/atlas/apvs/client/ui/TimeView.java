package ch.cern.atlas.apvs.client.ui;

import java.util.Iterator;
import java.util.Map;

import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class TimeView extends AbstractTimeView implements Module {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private HandlerRegistration measurementHandler;

	private String ptuId = null;
	private PtuSettings settings;
	private String measurementName = null;
	private EventBus cmdBus;
	private String options;

	public TimeView() {
	}
	
	@Override
	public boolean configure(Element element, ClientFactory clientFactory, Arguments args) {

		this.clientFactory = clientFactory;

		height = Integer.parseInt(args.getArg(0));
		cmdBus = clientFactory.getEventBus(args.getArg(1));
		options = args.getArg(2);
		measurementName = args.getArg(3);

		this.title = !options.contains("NoTitle");
		this.export = !options.contains("NoExport");

		PtuSettingsChangedEvent.subscribe(clientFactory.getRemoteEventBus(),
				new PtuSettingsChangedEvent.Handler() {

					@Override
					public void onPtuSettingsChanged(
							PtuSettingsChangedEvent event) {
						settings = event.getPtuSettings();
						updateChart();
					}
				});

		if (cmdBus != null) {
			SelectPtuEvent.subscribe(cmdBus, new SelectPtuEvent.Handler() {

				@Override
				public void onPtuSelected(final SelectPtuEvent event) {
					ptuId = event.getPtuId();
					updateChart();
				}
			});

			SelectMeasurementEvent.subscribe(cmdBus,
					new SelectMeasurementEvent.Handler() {

						@Override
						public void onSelection(SelectMeasurementEvent event) {
							measurementName = event.getName();
							updateChart();
						}
					});

			RequestEvent.register(cmdBus, new RequestEvent.Handler() {

				@Override
				public void onRequestEvent(RequestEvent event) {
					if (event.getRequestedClassName().equals(
							ColorMapChangedEvent.class.getName())) {
						cmdBus.fireEvent(new ColorMapChangedEvent(getColors()));
					}
				}
			});
		}
		
		return true;
	}

	private void updateChart() {
		unsubscribe();
		removeChart();

		if (measurementName.equals("")) {
			return;
		}

		if (ptuId == null) {
			// Subscribe to all PTUs
			final long t0 = System.currentTimeMillis();
			clientFactory.getPtuService().getHistories(measurementName,
					new AsyncCallback<Map<String, History>>() {

						@Override
						public void onSuccess(Map<String, History> histories) {
							if (histories == null) {
								return;
							}

							log.info("Histories Map retrieval of "
									+ measurementName + " took "
									+ (System.currentTimeMillis() - t0) + " ms");

							createChart(measurementName);

							int k = 0;
							for (Iterator<String> i = histories.keySet()
									.iterator(); i.hasNext();) {
								String ptuId = i.next();

								if ((settings == null)
										|| settings.isEnabled(ptuId)) {

									Series series = chart.createSeries()
											.setName(getName(ptuId));
									pointsById.put(ptuId, 0);
									seriesById.put(ptuId, series);
									colorsById.put(ptuId, color[k]);

									addHistory(ptuId, series,
											histories.get(ptuId));

									chart.addSeries(series, true, false);
									k++;
								}
							}

							add(chart);

							if (cmdBus != null) {
								ColorMapChangedEvent.fire(cmdBus, getColors());
							}

							subscribe(null, measurementName);
						}

						@Override
						public void onFailure(Throwable caught) {
							log.warn("Cannot retrieve Measurements ", caught);
						}
					});
		} else {
			// subscribe to single PTU
			log.info("***** " + ptuId);
			if ((settings == null) || settings.isEnabled(ptuId)) {

				final long t0 = System.currentTimeMillis();
				clientFactory.getPtuService().getHistory(ptuId,
						measurementName, new AsyncCallback<History>() {

							@Override
							public void onSuccess(History history) {
								if (history == null) {
									log.warn("Cannot find history for "
											+ measurementName);
								}

								log.info("Measurement retrieval of "
										+ measurementName + " of " + ptuId
										+ " took "
										+ (System.currentTimeMillis() - t0)
										+ " ms");

								createChart(measurementName + " (" + ptuId
										+ ")");

								Series series = chart.createSeries().setName(
										getName(ptuId));
								pointsById.put(ptuId, 0);
								seriesById.put(ptuId, series);
								colorsById.put(ptuId, color[0]);

								addHistory(ptuId, series, history);

								chart.addSeries(series, true, false);

								add(chart);

								cmdBus.fireEvent(new ColorMapChangedEvent(
										getColors()));

								subscribe(ptuId, measurementName);
							}

							@Override
							public void onFailure(Throwable caught) {
								log.warn("Cannot retrieve Measurements ",
										caught);
							}
						});
			}
		}
	}

	private String getName(String ptuId) {
		return (settings != null && settings.getName(ptuId) != null
				&& !settings.getName(ptuId).equals("") ? settings
				.getName(ptuId) + " - " : "")
				+ "" + ptuId;
	}

	private void addHistory(String ptuId, Series series, History history) {
		if (history == null)
			return;

		Number[][] data = history.getData();
		series.setPoints(data != null ? data : new Number[0][2], false);
		pointsById.put(ptuId, data != null ? data.length : 0);

		setUnit(history.getUnit());
	}

	private void unsubscribe() {
		if (measurementHandler != null) {
			measurementHandler.removeHandler();
			measurementHandler = null;
		}
	}

	private void subscribe(final String ptuId, final String name) {
		unsubscribe();

		measurementHandler = MeasurementChangedEvent.subscribe(
				clientFactory.getRemoteEventBus(),
				new MeasurementChangedEvent.Handler() {

					@Override
					public void onMeasurementChanged(
							MeasurementChangedEvent event) {
						Measurement m = event.getMeasurement();

						if ((ptuId != null) && (!m.getPtuId().equals(ptuId))) {
							return;
						}

						if (m.getName().equals(name)) {
							log.info("New meas " + m);
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

								setUnit(m.getUnit());

								series.addPoint(m.getDate().getTime(),
										m.getValue(), true, shift, true);
							}
						}
					}
				});
	}
}
