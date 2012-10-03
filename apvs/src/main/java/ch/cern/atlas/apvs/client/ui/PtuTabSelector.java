package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.client.tablet.LocalStorage;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class PtuTabSelector extends HorizontalPanel {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private RemoteEventBus remoteEventBus;
	private List<EventBus> eventBusses = new ArrayList<EventBus>();

	private List<String> ptuIds;
	private String ptuId;
	private PtuSettings settings;
	private List<String> extraTabs;

	public PtuTabSelector(ClientFactory clientFactory, Arguments args) {

		super();
		
//		add(new Brand("AWSS"));

		remoteEventBus = clientFactory.getRemoteEventBus();

		String[] busNames = args.getArg(0).split(",");
		for (int i = 0; i < busNames.length; i++) {
			eventBusses.add(clientFactory.getEventBus(busNames[i].trim()));
		}
		extraTabs = args.getArgs(1);

		PtuSettingsChangedEvent.subscribe(remoteEventBus,
				new PtuSettingsChangedEvent.Handler() {

					@Override
					public void onPtuSettingsChanged(
							PtuSettingsChangedEvent event) {
						settings = event.getPtuSettings();

						update();
					}
				});

		PtuIdsChangedEvent.subscribe(remoteEventBus,
				new PtuIdsChangedEvent.Handler() {

					@Override
					public void onPtuIdsChanged(PtuIdsChangedEvent event) {

						ptuIds = event.getPtuIds();

						ptuId = LocalStorage.getInstance().get(
								LocalStorage.PTU_ID);
						fireEvent(new SelectPtuEvent(ptuId));

						if (ptuId != null) {
							Tabs.setCurrentTab("Ptu");
						}

						update();
					}
				});
	}

	private String getName(String id) {
		return (settings != null) && !settings.getName(id).equals("") ? settings
				.getName(id) + " (" + id.toString() + ")"
				: id.toString();
	}

	private String getId(String name) {
		int open = name.indexOf('(');
		int close = name.lastIndexOf(')');
		if ((open >= 0) && (close >= 0)) {
			name = name.substring(open + 1, close);
		}
		log.info(name);
		return name;
	}

	private void update() {
		clear();
		if (ptuIds != null) {

			Collections.sort(ptuIds);

			for (Iterator<String> i = ptuIds.iterator(); i.hasNext();) {
				String id = i.next();
				if ((settings != null) && !settings.isEnabled(id))
					continue;

				ToggleButton b = new ToggleButton(getName(id));
				b.setDown(id.equals(ptuId));

				b.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						ptuId = null;

						ToggleButton b = (ToggleButton) event.getSource();
						ptuId = getId(b.getText());

						radio(b);

						Tabs.setCurrentTab("Ptu");

						LocalStorage.getInstance().put(LocalStorage.PTU_ID,
								ptuId);
						fireEvent(new SelectPtuEvent(ptuId));
					}
				});
				add(b);
			}
		}

		for (Iterator<String> i = extraTabs.iterator(); i.hasNext();) {
			ToggleButton b = new ToggleButton(i.next());
			b.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					ptuId = null;

					ToggleButton b = (ToggleButton) event.getSource();
					radio(b);

					Tabs.setCurrentTab(b.getText());

					LocalStorage.getInstance().put(LocalStorage.PTU_ID, ptuId);
					fireEvent(new SelectPtuEvent(ptuId));
				}
			});
			add(b);
		}
	}

	public String getPtuId() {
		return ptuId;
	}

	private void radio(ToggleButton b) {
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof ToggleButton) {
				ToggleButton t = (ToggleButton) w;
				t.setDown(w == b);
			}
		}
	}

	private void fireEvent(SelectPtuEvent event) {
		for (Iterator<EventBus> i = eventBusses.iterator(); i.hasNext();) {
			i.next().fireEvent(event);
		}
	}
}
