package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.SettingsChangedEvent;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

public class SettingsPersister extends VerticalFlowPanel {

	private final static String APVS_SETTINGS = "APVS.settings";
	private SettingsFactory settingsFactory = GWT.create(SettingsFactory.class);
	private Settings settings;
	private RemoteEventBus eventBus;

	public SettingsPersister(final RemoteEventBus eventBus) {
		this.eventBus = eventBus;

		settings = settingsFactory.settings().as();

		try {
			load();
		} catch (Exception e) {
			Window.alert("Could not load settings, continuing... \n"
					+ e.getMessage());
			e.printStackTrace();
		}

		SettingsChangedEvent.register(eventBus,
				new SettingsChangedEvent.Handler() {

					@Override
					public void onSettingsChanged(SettingsChangedEvent event) {
						settings = event.getSettings();

						store();
					}
				});

		RequestEvent.register(eventBus, new RequestEvent.Handler() {

			@Override
			public void onRequestEvent(RequestEvent event) {
				System.err.println("Request " + event.getRequestedClassName());
				if (event.getRequestedClassName().equals(
						SettingsChangedEvent.class.getName())) {
					eventBus.fireEvent(new SettingsChangedEvent(settings));
				}
			}
		});
	}

	private void load() {
		Storage store = Storage.getLocalStorageIfSupported();
		if (store == null) {
			Window.alert("Settings will not be stored");
			return;
		}
		for (int i = 0; i < store.getLength(); i++) {
			String key = store.key(i);
			System.err.println(key + " " + store.getItem(key));
		}
		String json = store.getItem(APVS_SETTINGS);
		// String json = null;
		System.err.println("get " + json);

		settings = settingsFactory.settings().as();
		if (json != null) {
			AutoBean<Settings> bean = AutoBeanCodex.decode(settingsFactory,
					Settings.class, json);
			settings = bean.as();
		}

		eventBus.fireEvent(new SettingsChangedEvent(settings));
	}

	private void store() {
		Storage store = Storage.getLocalStorageIfSupported();
		if (store == null)
			return;

		AutoBean<Settings> bean = AutoBeanUtils.getAutoBean(settings);
		String json = AutoBeanCodex.encode(bean).getPayload();

		System.err.println("set " + json);
		store.setItem(APVS_SETTINGS, json);
		System.err.println(store.getLength());
	}
}
