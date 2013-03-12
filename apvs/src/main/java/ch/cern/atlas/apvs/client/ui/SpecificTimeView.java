package ch.cern.atlas.apvs.client.ui;

import java.util.Date;

import org.moxieapps.gwt.highcharts.client.Chart;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.domain.HistoryMap;
import ch.cern.atlas.apvs.client.domain.Intervention;
import ch.cern.atlas.apvs.client.domain.InterventionMap;
import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;

import com.google.web.bindery.event.shared.HandlerRegistration;

public class SpecificTimeView extends AbstractTimeView {

	private HandlerRegistration measurementHandler;

	public SpecificTimeView() {
	}

	public Chart createSingleChart(ClientFactory factory, String measurementName, String ptuId,
			HistoryMap historyMap, InterventionMap interventions, boolean showLimits) {
		createChart(Measurement.getDisplayName(measurementName) + " (" + ptuId
				+ ")");
		add(chart);

		addSeries(ptuId, getName(ptuId, interventions), showLimits);
		addHistory(historyMap.get(ptuId, measurementName));

		register(factory, measurementName, ptuId);
		
		return chart;
	}

	protected String getName(String ptuId, InterventionMap interventions) {
		if (interventions == null) {
			return ptuId;
		}
		Intervention intervention = interventions.get(ptuId);
		return ((intervention != null) && !intervention.getName().equals("") ? intervention
				.getName() + " - "
				: "")
				+ "" + ptuId;
	}

	protected void addHistory(History history) {
		if (history == null)
			return;

		setData(history.getPtuId(), history.getData(), history.getLimits());

		setUnit(history.getPtuId(), history.getUnit());
	}

	protected void unregister() {
		if (measurementHandler != null) {
			measurementHandler.removeHandler();
			measurementHandler = null;
		}
	}

	private final static long MINUTE = 60 * 1000; // 1 minute

	protected void register(ClientFactory factory, final String measurementName, final String ptuId) {
		unregister();

		measurementHandler = MeasurementChangedEvent.register(
				factory.getRemoteEventBus(),
				new MeasurementChangedEvent.Handler() {

					@Override
					public void onMeasurementChanged(
							MeasurementChangedEvent event) {
						Measurement m = event.getMeasurement();
						if (m.getName().equals(measurementName)
								&& ((ptuId == null) || m.getPtuId().equals(
										ptuId))
								&& (m.getDate().getTime() < new Date()
										.getTime() + MINUTE)) {
							addPoint(m.getPtuId(), m.getDate().getTime(),
									m.getValue(), m.getLowLimit(),
									m.getHighLimit());

							setUnit(m.getPtuId(), m.getUnit());
						}
					}
				});
	}

}
