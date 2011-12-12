package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.event.SupervisorSettingsChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.TextBox;

public class NameSelector extends TextBox {

	private SupervisorSettings settings;
	private Integer ptuId;
	private String name;

	public NameSelector(RemoteEventBus remoteEventBus,
			RemoteEventBus localEventBus) {

		setEnabled(false);

		SupervisorSettingsChangedEvent.subscribe(remoteEventBus,
				new SupervisorSettingsChangedEvent.Handler() {

					@Override
					public void onSupervisorSettingsChanged(
							SupervisorSettingsChangedEvent event) {
						settings = event.getSupervisorSettings();

						retrieveName();

						update();
					}
				});

		SelectPtuEvent.subscribe(localEventBus, new SelectPtuEvent.Handler() {

			@Override
			public void onPtuSelected(SelectPtuEvent event) {
				ptuId = event.getPtuId();

				retrieveName();

				update();
			}
		});
	}

	private void retrieveName() {
		if ((settings == null) || (ptuId == null))
			return;

		name = settings.getName(Settings.DEFAULT_SUPERVISOR, ptuId);
	}

	private void update() {
		setText(name);
	}
}
