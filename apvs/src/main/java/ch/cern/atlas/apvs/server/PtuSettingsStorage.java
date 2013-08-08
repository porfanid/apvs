package ch.cern.atlas.apvs.server;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.settings.Proxy;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.client.ui.CameraView;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestRemoteEvent;

public class PtuSettingsStorage {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private static final String APVS_PTU_SETTINGS = "APVS.ptu.settings";
	private static PtuSettingsStorage instance;
	
	private final static boolean DEBUG = false;
	
	private PtuSettings settings;

	public PtuSettingsStorage(final RemoteEventBus eventBus) {

		load();

		PtuSettingsChangedRemoteEvent.register(eventBus,
				new PtuSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onPtuSettingsChanged(
							PtuSettingsChangedRemoteEvent event) {
						settings = event.getPtuSettings();

						store();
					}
				});
		
		InterventionMapChangedRemoteEvent.subscribe(eventBus, new InterventionMapChangedRemoteEvent.Handler() {
			
			@Override
			public void onInterventionMapChanged(InterventionMapChangedRemoteEvent event) {
				if (DEBUG) {
					log.info("PTU Setting Storage: PTU IDS changed");
				}
				List<String> activePtuIds = event.getInterventionMap().getPtuIds();

				boolean changed = false;
				for (Iterator<String> i = activePtuIds.iterator(); i
						.hasNext();) {
					boolean added = settings.add(i.next());
					changed |= added;
				}

				if (changed) {
					eventBus.fireEvent(new PtuSettingsChangedRemoteEvent(
							settings));
				}
			}
		});

		RequestRemoteEvent.register(eventBus, new RequestRemoteEvent.Handler() {

			@Override
			public void onRequestEvent(RequestRemoteEvent event) {
				if (event.getRequestedClassName().equals(
						PtuSettingsChangedRemoteEvent.class.getName())) {
					eventBus.fireEvent(new PtuSettingsChangedRemoteEvent(settings));
				}
			}
		});

		eventBus.fireEvent(new PtuSettingsChangedRemoteEvent(settings));
	}

	public static PtuSettingsStorage getInstance(RemoteEventBus eventBus) {
		if (instance == null) {
			instance = new PtuSettingsStorage(eventBus);
		}
		return instance;
	}

	private void load() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			log.warn("Ptu Settings will not be stored");
			return;
		}

		Proxy proxy = new Proxy(false, "");
		
		settings = new PtuSettings();
		for (Iterator<String> i = store.getKeys(APVS_PTU_SETTINGS).iterator(); i.hasNext(); ) {
			String ptuId = i.next();
			
			settings.add(ptuId);
			
			settings.setEnabled(ptuId, store.getBoolean(APVS_PTU_SETTINGS+"."+ptuId+".enabled"));
			settings.setDosimeterSerialNumber(ptuId, store.getString(APVS_PTU_SETTINGS+"."+ptuId+".dosimeterSerialNo"));
			settings.setCameraUrl(ptuId, CameraView.HELMET, store.getString(APVS_PTU_SETTINGS+"."+ptuId+".helmetUrl"), proxy);
			settings.setCameraUrl(ptuId, CameraView.HELMET, store.getString(APVS_PTU_SETTINGS+"."+ptuId+".handUrl"), proxy);
		}
	}

	private void store() {
		ServerStorage store = ServerStorage.getLocalStorageIfSupported();
		if (store == null) {
			return;
		}
		
		Proxy proxy = new Proxy(false, "");

		for (Iterator<String> i = settings.getPtuIds().iterator(); i.hasNext();) {
			String ptuId = i.next();
			store.setItem(APVS_PTU_SETTINGS + "." + ptuId+".enabled", settings.isEnabled(ptuId));
			store.setItem(APVS_PTU_SETTINGS + "." + ptuId+".dosimeterSerialNo", settings.getDosimeterSerialNumber(ptuId));
			store.setItem(APVS_PTU_SETTINGS + "." + ptuId+".helmetUrl", settings.getCameraUrl(ptuId, CameraView.HELMET, proxy));
			store.setItem(APVS_PTU_SETTINGS + "." + ptuId+".handUrl", settings.getCameraUrl(ptuId, CameraView.HAND, proxy));
		}
	}
}
