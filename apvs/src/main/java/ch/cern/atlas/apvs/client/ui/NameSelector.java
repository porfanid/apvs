package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.event.SupervisorSettingsChangedEvent;
import ch.cern.atlas.apvs.client.settings.Settings;
import ch.cern.atlas.apvs.client.settings.SupervisorSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.TextBox;

public class NameSelector extends TextBox {

	private SupervisorSettings settings;
	private Integer ptuId;

	public NameSelector(RemoteEventBus remoteEventBus,
			RemoteEventBus localEventBus) {

		setEnabled(false);

		SupervisorSettingsChangedEvent.subscribe(remoteEventBus,
				new SupervisorSettingsChangedEvent.Handler() {

					@Override
					public void onSupervisorSettingsChanged(
							SupervisorSettingsChangedEvent event) {
						settings = event.getSupervisorSettings();

						update();
					}
				});

		SelectPtuEvent.subscribe(localEventBus, new SelectPtuEvent.Handler() {

			@Override
			public void onPtuSelected(SelectPtuEvent event) {
				ptuId = event.getPtuId();

				update();
			}
		});
	}

	public String getName() {
		if ((settings == null) || (ptuId == null))
			return "";

		return settings.getName(Settings.DEFAULT_SUPERVISOR, ptuId);
	}

	private void update() {
		setText(getName());
	}
}
