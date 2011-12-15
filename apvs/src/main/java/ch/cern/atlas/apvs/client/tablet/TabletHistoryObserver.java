package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.event.NavigateStepEvent;
import ch.cern.atlas.apvs.client.event.SelectStepEvent;
import ch.cern.atlas.apvs.client.tablet.ModelEntrySelectedEvent.ModelEntry;
import ch.cern.atlas.apvs.client.tablet.ProcedureEntrySelectedEvent.ProcedureEntry;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.dom.client.event.mouse.HandlerRegistrationCollection;
import com.googlecode.mgwt.mvp.client.history.HistoryHandler;
import com.googlecode.mgwt.mvp.client.history.HistoryObserver;
import com.googlecode.mgwt.ui.client.MGWT;

public class TabletHistoryObserver implements HistoryObserver {

	@Override
	public void onPlaceChange(Place place, HistoryHandler handler) {

	}

	@Override
	public void onHistoryChanged(Place place, HistoryHandler handler) {

	}

	@Override
	public void onAppStarted(Place place, HistoryHandler historyHandler) {
		if (MGWT.getOsDetection().isPhone()) {
			// onPhoneNav(place, historyHandler);
			onTabletNav(place, historyHandler);
		} else {
			// tablet
			onTabletNav(place, historyHandler);

		}

	}

	@Override
	public HandlerRegistration bind(EventBus eventBus,
			final HistoryHandler historyHandler) {

		HandlerRegistration register4 = SelectStepEvent.register(eventBus, new SelectStepEvent.Handler() {
			
			@Override
			public void onSelectStep(SelectStepEvent event) {
				// FIXME should be some setStepEvent
				System.err.println("STEP "+event.getStep());
				
				Place place = new ProcedurePlace("FIXME", "TileDrawerExtraction", Integer.toString(event.getStep()));
				
				if (MGWT.getOsDetection().isTablet()) {
					historyHandler.replaceCurrentPlace(place);
					historyHandler.goTo(place, true);
				} else {
					historyHandler.goTo(place);
				}
			}
		});
		
		HandlerRegistration register3 = ProcedureEntrySelectedEvent.register(eventBus,
				new ProcedureEntrySelectedEvent.Handler() {

					@Override
					public void onProcedureEntrySelected(ProcedureEntrySelectedEvent event) {

						ProcedureEntry entry = event.getEntry();

						Place place = null;

						switch (entry) {
						case MURAL_PAINTING:
							place = new ProcedurePlace("FIXME", "mural", "1");
							break;
						case TILE_DRAWER_EXTRACTION:
							place = new ProcedurePlace("FIXME", "TileDrawerExtraction", "1");
							break;

						default:
							break;
						}

						if (MGWT.getOsDetection().isTablet()) {
							historyHandler.replaceCurrentPlace(place);
							historyHandler.goTo(place, true);
						} else {
							historyHandler.goTo(place);
						}

					}
				});

		HandlerRegistration register2 = ModelEntrySelectedEvent.register(eventBus,
				new ModelEntrySelectedEvent.Handler() {

					@Override
					public void onModelEntrySelected(ModelEntrySelectedEvent event) {

						ModelEntry entry = event.getEntry();

						Place place = null;

						switch (entry) {
						case RUN_LAYOUT:
							place = new ImagePlace("Run Layout", "images/RunLayout.png");
							break;
						case TILE_CAL_BARREL_3D:
							place = new ImagePlace("Tile Cal Barrel 3D", "images/TileCalBarrel3D.png");
							break;
						case TILE_CAL_BARREL_DWG:
							place = new ImagePlace("Tile Cal Barrel DWG", "images/TileCalBarrelDwg.png");
							break;

						default:
							break;
						}

						if (MGWT.getOsDetection().isTablet()) {
							historyHandler.replaceCurrentPlace(place);
							historyHandler.goTo(place, true);
						} else {
							historyHandler.goTo(place);
						}

					}
				});

		HandlerRegistration register1 = ActionEvent.register(eventBus,
				ActionNames.BACK, new ActionEvent.Handler() {

					@Override
					public void onAction(ActionEvent event) {

						History.back();

					}
				});

		HandlerRegistrationCollection col = new HandlerRegistrationCollection();
		col.addHandlerRegistration(register1);
		col.addHandlerRegistration(register2);
		col.addHandlerRegistration(register3);
		col.addHandlerRegistration(register4);
		return col;
	}

	private void onTabletNav(Place place, HistoryHandler historyHandler) {
//		if (place instanceof AboutPlace) {
//			historyHandler.replaceCurrentPlace(new HomePlace());
//		}
	}

}
