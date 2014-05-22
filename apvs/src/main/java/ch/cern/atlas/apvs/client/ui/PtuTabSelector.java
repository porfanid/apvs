package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.gwtbootstrap3.client.ui.AnchorButton;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListDropDown;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.NavbarCollapse;
import org.gwtbootstrap3.client.ui.NavbarNav;
import org.gwtbootstrap3.client.ui.constants.NavbarPull;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.UnorderedList;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.PtuSettingsChangedRemoteEvent;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.event.SelectTabEvent;
import ch.cern.atlas.apvs.client.settings.LocalStorage;
import ch.cern.atlas.apvs.client.settings.PtuSettings;
import ch.cern.atlas.apvs.client.widget.UpdateScheduler;
import ch.cern.atlas.apvs.domain.Device;
import ch.cern.atlas.apvs.domain.InterventionMap;
import ch.cern.atlas.apvs.event.InterventionMapChangedRemoteEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.eventbus.shared.RequestEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;

public class PtuTabSelector extends NavbarCollapse implements Module {

	private RemoteEventBus remoteEventBus;
	private List<EventBus> eventBusses = new ArrayList<EventBus>();

	private NavbarNav nav;
	private AnchorButton dropDown;

	private List<Device> ptus;
	private Device selectedPtu;
	private String selectedTab;
	private PtuSettings settings;
	private InterventionMap interventions;
	private List<String> extraTabs;

	private List<ListItem> items;

	private UpdateScheduler scheduler = new UpdateScheduler(this);

	public PtuTabSelector() {
	}

	@Override
	public boolean configure(Element element, ClientFactory clientFactory,
			Arguments args) {
		setId("navbar-collapse");

		remoteEventBus = clientFactory.getRemoteEventBus();

		String[] busNames = args.getArg(0).split(",");
		for (int i = 0; i < busNames.length; i++) {
			eventBusses.add(clientFactory.getEventBus(busNames[i].trim()));
		}
		extraTabs = args.getArgs(1);

		selectedTab = LocalStorage.getInstance().get(LocalStorage.SELECTED_TAB);
		selectedPtu = null;

		// listen to all event busses
		for (final EventBus eventBus : eventBusses) {
			RequestEvent.register(this, eventBus, new RequestEvent.Handler() {

				@Override
				public void onRequestEvent(RequestEvent event) {
					String eventType = event.getRequestedClassName();
					if (eventType.equals(SelectPtuEvent.class.getName())) {
						eventBus.fireEvent(new SelectPtuEvent(selectedPtu));
					} else if (eventType.equals(SelectTabEvent.class.getName())) {
						eventBus.fireEvent(new SelectTabEvent(selectedTab));
					}
				}
			});
		}

		PtuSettingsChangedRemoteEvent.subscribe(this, remoteEventBus,
				new PtuSettingsChangedRemoteEvent.Handler() {

					@Override
					public void onPtuSettingsChanged(
							PtuSettingsChangedRemoteEvent event) {
						settings = event.getPtuSettings();

						scheduler.update();
					}
				});

		InterventionMapChangedRemoteEvent.subscribe(this, remoteEventBus,
				new InterventionMapChangedRemoteEvent.Handler() {

					@Override
					public void onInterventionMapChanged(
							InterventionMapChangedRemoteEvent event) {

						interventions = event.getInterventionMap();

						ptus = interventions.getPtus();

						scheduler.update();

						if (selectedTab != null) {
							if (selectedPtu == null) {
								for (Device device : ptus) {
									if (device.getName().equals(selectedTab)) {
										selectedPtu = device;
										break;
									}
								}
							}

							fireEvent(new SelectTabEvent(
									selectedPtu == null ? selectedTab : "Ptu"));
							fireEvent(new SelectPtuEvent(selectedPtu));
						}
					}
				});

		return true;
	}

	private String getName(Device device) {
		return (interventions != null) && (interventions.get(device) != null)
				&& !interventions.get(device).getName().equals("") ? interventions
				.get(device).getName() + " (" + device.getName() + ")"
				: device.getName();
	}

	@Override
	public boolean update() {
		clear();
		nav = new NavbarNav();
		add(nav);

		items = new ArrayList<ListItem>();
		dropDown = null;

		if (ptus != null) {

			Collections.sort(ptus);

			UnorderedList list = nav;

			if (ptus.size() > 1) {
				// create dropdown menu
				ListDropDown dropDownList = new ListDropDown();
				nav.add(dropDownList);

				dropDown = new AnchorButton();
				dropDown.setDataToggle(Toggle.DROPDOWN);
				dropDownList.add(dropDown);

				list = new DropDownMenu();
				dropDownList.add(list);
			}

			for (Iterator<Device> i = ptus.iterator(); i.hasNext();) {
				final Device ptu = i.next();

				if ((settings != null) && !settings.isEnabled(ptu.getName())) {
					continue;
				}

				final ListItem item = new ListItem();
				Label label = new Label(getName(ptu));
				item.add(label);
				item.setActive(ptu.getName().equals(selectedTab));

				label.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {

						selectedTab = ptu.getName();
						selectedPtu = ptu;

						radio(item);
						setDropdown();
						
						fireEvent(new SelectTabEvent("Ptu"));
						LocalStorage.getInstance().put(
								LocalStorage.SELECTED_TAB, selectedTab);

						fireEvent(new SelectPtuEvent(selectedPtu));
						LocalStorage.getInstance().put(
								LocalStorage.SELECTED_PTU_ID,
								selectedPtu != null ? selectedPtu.getName()
										: null);
					}
				});
				list.add(item);
				items.add(item);
			}
		}

		for (Iterator<String> i = extraTabs.iterator(); i.hasNext();) {
			String name = i.next();
			switch (name) {
			case ">":
				nav = new NavbarNav();
				nav.setPull(NavbarPull.RIGHT);
				add(nav);
				continue;
			case "=":
				nav = new NavbarNav();
				nav.setPull(NavbarPull.LEFT);
				add(nav);
				continue;
			case "<":
				nav = new NavbarNav();
				nav.setPull(NavbarPull.NONE);
				add(nav);
				continue;
			default:
				if (name.endsWith("+")) {
					name = name.substring(0, name.length() - 1);
					if ((ptus == null) || (ptus.size() == 0)) {
						// bail out, no interventions
						continue;
					}
				}
				break;
			}

			final ListItem item = new ListItem();
			final Label label = new Label(name);
			item.setActive(name.equals(selectedTab));

			label.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					selectedTab = label.getText();
					selectedPtu = null;
				
					radio(item);
					setDropdown();

					fireEvent(new SelectTabEvent(selectedTab));
					LocalStorage.getInstance().put(LocalStorage.SELECTED_TAB,
							selectedTab);

					fireEvent(new SelectPtuEvent(selectedPtu));
					LocalStorage.getInstance().put(
							LocalStorage.SELECTED_PTU_ID,
							selectedPtu != null ? selectedPtu.getName() : null);
				}
			});
			nav.add(item);
			items.add(item);
		}

		setDropdown();

		return false;
	}

	private void setDropdown() {
		if (dropDown != null) {
			if (selectedPtu == null) {
				dropDown.setText("PTU");
				dropDown.setActive(false);
			} else {
				dropDown.setText(getName(selectedPtu));
				dropDown.setActive(selectedPtu.getName().equals(selectedTab));
			}
		}
	}

	private void radio(ListItem b) {
		for (ListItem i : items) {
			i.setActive(i == b);
		}
	}

	private void fireEvent(Event<?> event) {
		for (Iterator<EventBus> i = eventBusses.iterator(); i.hasNext();) {
			EventBus eventBus = i.next();
			eventBus.fireEvent(event);
		}
	}
}
