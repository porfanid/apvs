package ch.cern.atlas.apvs.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.tablet.LocalStorage;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;
import ch.cern.atlas.apvs.ptu.shared.PtuIdsChangedEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class PtuTabSelector extends HorizontalPanel implements ClickHandler {

	private RemoteEventBus remoteEventBus;
	private List<EventBus> eventBusses = new ArrayList<EventBus>();
	
	private List<Integer> ptuIds;
	private Integer ptuId;

	public PtuTabSelector(ClientFactory clientFactory,
			 Arguments args) {
		
		super();
		
		remoteEventBus = clientFactory.getRemoteEventBus();
		
		String[] busNames = args.getArg(0).split(",");
		for (int i=0; i<busNames.length; i++) {
			eventBusses.add(clientFactory.getEventBus(busNames[i].trim()));
		}
		
		PtuIdsChangedEvent.subscribe(remoteEventBus,
				new PtuIdsChangedEvent.Handler() {

					@Override
					public void onPtuIdsChanged(PtuIdsChangedEvent event) {

						ptuIds = event.getPtuIds();

						ptuId = LocalStorage.getInstance().getInteger(
								LocalStorage.PTU_ID);
						fireEvent(new SelectPtuEvent(ptuId));
						
						update();
					}
				});
	}

	private void update() {
		if (ptuIds != null) {
			Collections.sort(ptuIds);
		}

//		int selectedIndex = getSelectedIndex();
		// FIXME
//		String selectedItem = selectedIndex < 0 ? null : getTabWidget(selectedIndex).toString();

		clear();
		for (Iterator<Integer> i = ptuIds.iterator(); i.hasNext(); ) {
			Integer id = i.next();
			ToggleButton b = new ToggleButton(id.toString());
			if (id.equals(ptuId)) {
				b.setDown(true);
			}
			b.addClickHandler(this);
			add(b);			
		}
	}

	public Integer getPtuId() {
		return ptuId;
	}

	@Override
	public void onClick(ClickEvent event) {
		ptuId = null;

		ToggleButton b = (ToggleButton)event.getSource();
		System.err.println("Selected "+b.getText());
		ptuId = Integer.parseInt(b.getText());
		
		for (int i=0; i<getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if ((w != b) && (w instanceof ToggleButton)) {
				((ToggleButton)w).setDown(false);
			}
		}
		
		LocalStorage.getInstance().put(LocalStorage.PTU_ID, ptuId);
		fireEvent(new SelectPtuEvent(ptuId));
	}

	private void fireEvent(SelectPtuEvent event) {
		for (Iterator<EventBus> i = eventBusses.iterator(); i.hasNext(); ) {
			i.next().fireEvent(event);
		}
	}
}
