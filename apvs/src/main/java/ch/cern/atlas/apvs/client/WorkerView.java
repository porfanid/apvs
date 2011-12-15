package ch.cern.atlas.apvs.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.cern.atlas.apvs.client.places.Acquisition;
import ch.cern.atlas.apvs.client.places.MenuPlace;
import ch.cern.atlas.apvs.client.places.Models;
import ch.cern.atlas.apvs.client.places.Procedures;
import ch.cern.atlas.apvs.client.places.RadiationMapping;
import ch.cern.atlas.apvs.client.places.Settings;
import ch.cern.atlas.apvs.client.widget.VerticalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkerView extends DockPanel {

	public WorkerView(RemoteEventBus remoteEventBus) {
//		setSize("1024px", "768px");
		
		VerticalFlowPanel west = new VerticalFlowPanel();
		west.setWidth("274px");
		add(west, WEST);
		west.add(new PtuSelector(remoteEventBus, remoteEventBus));
		west.add(getStackedMenu());
		west.add(new MeasurementView(remoteEventBus, remoteEventBus));
		
		VerticalFlowPanel east = new VerticalFlowPanel();
		add(east, CENTER);
		east.add(new ProcedureView(remoteEventBus, remoteEventBus, 750, 730));
		east.add(new ProcedureControls(remoteEventBus));
	}

	private Widget getStackedMenu() {
		final List<MenuPlace> places = new ArrayList<MenuPlace>();
		places.add(new Settings());
		places.add(new Procedures());
		places.add(new Acquisition());
		places.add(new Models());
		places.add(new RadiationMapping());
//		places.add(new Log());

		final StackPanel stackPanel = new StackPanel();
//		stackPanel.setPixelSize(200, 400);

		for (Iterator<MenuPlace> i = places.iterator(); i.hasNext();) {
			MenuPlace menuPlace = i.next();
			stackPanel.add(menuPlace.getWidget(), menuPlace.getHeader(), true);
		}
/*
		stackPanel.add(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				RemotePlace place = places.get(event.getSelectedItem());
				// place.setRemoteID(client.getConnectionID());
//				placeController.goTo(place);
			}
		});
		*/
		/*
		 * eventBus.addHandler(PlaceChangeEvent.TYPE, new
		 * PlaceChangeEvent.Handler() {
		 * 
		 * @Override public void onPlaceChange(PlaceChangeEvent event) { Place
		 * place = event.getNewPlace(); if (place instanceof MenuPlace) {
		 * MenuPlace menuPlace = (MenuPlace) place;
		 * stackLayoutPanel.showWidget(menuPlace.getIndex(), false); } } });
		 */
		return stackPanel;
	}

}
