package ch.cern.atlas.apvs.client.manager;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.HistoryChangedEvent;
import ch.cern.atlas.apvs.domain.Data;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.History;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.MeasurementChangedEvent;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class HistoryManager {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private static HistoryManager instance;
	private History history;
	private List<Device> ptus;
	private HandlerRegistration measurementRegistration;
	private ClientFactory clientFactory;
	private RemoteEventBus eventBus;

	private HistoryManager(ClientFactory clientFactory) throws SerializationException {
		this.clientFactory = clientFactory;
		this.eventBus = clientFactory.getRemoteEventBus();
		
		// subscribe
		InterventionMapChangedRemoteEvent.subscribe(eventBus,
				new InterventionMapChangedRemoteEvent.Handler() {

					@Override
					public void onInterventionMapChanged(
							InterventionMapChangedRemoteEvent event) {
						// FIXME #298
						System.err
								.println("FIXME (3x?)... Received............."
										+ event.getEventBusUUID() + " "
										+ event.getSourceUUID());
						List<Device> newPtus = event.getInterventionMap()
								.getPtus();
						if (!newPtus.equals(ptus)) {
							ptus = newPtus;
							update();
						}
					}
				});
	}

	private void update() {
		if (measurementRegistration != null) {
			measurementRegistration.removeHandler();
			measurementRegistration = null;
		}

		long now = new Date().getTime();
		// #502, reduce to one hour for now
		Date yesterday = new Date(now - (/* 24 */4 * 60 * 60 * 1000));

		history = new History();
		clientFactory.getPtuService().getHistory(ptus, yesterday, 2000,
				new AsyncCallback<History>() {

					@Override
					public void onSuccess(History result) {
						history = result;

						subscribe();

						HistoryChangedEvent.fire(eventBus, history);
					}

					@Override
					public void onFailure(Throwable caught) {
						log.warn("Cannot get histories " + caught);
					}
				});
	}

	public static HistoryManager getInstance(ClientFactory clientFactory)
			throws SerializationException {
		if (instance == null) {
			instance = new HistoryManager(clientFactory);
		}
		return instance;
	}

	private void subscribe() {
		// subscribe to further measurements
		measurementRegistration = MeasurementChangedEvent.register(eventBus,
				new MeasurementChangedEvent.Handler() {

					@Override
					public void onMeasurementChanged(
							MeasurementChangedEvent event) {
						// Add entry to history, in the
						// correct place
						Measurement measurement = event.getMeasurement();
						Data data = history.get(measurement.getDevice(),
								measurement.getSensor());
						if (data == null) {
							data = new Data(measurement.getDevice(),
									measurement.getSensor(), measurement
											.getUnit(), 2000);
							history.put(data);
						}
						if (measurement.getTime().getTime() < new Date()
								.getTime() + 60000) {
							data.addEntry(measurement.getTime().getTime(),
									measurement.getValue(),
									measurement.getDownThreshold(),
									measurement.getUpThreshold(),
									measurement.getSamplingRate());
						}

					}
				});
	}

}
