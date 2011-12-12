package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.dom.client.event.mouse.HandlerRegistrationCollection;
import com.googlecode.mgwt.mvp.client.history.HistoryHandler;
import com.googlecode.mgwt.mvp.client.history.HistoryObserver;
import com.googlecode.mgwt.ui.client.MGWT;

public class ApvsHistoryObserver implements HistoryObserver {

	@Override
	public void onPlaceChange(Place place, HistoryHandler handler) {

	}

	@Override
	public void onHistoryChanged(Place place, HistoryHandler handler) {

	}

	@Override
	public void onAppStarted(Place place, HistoryHandler historyHandler) {
		if (MGWT.getOsDetection().isPhone()) {
//			onPhoneNav(place, historyHandler);
			onTabletNav(place, historyHandler);
		} else {
			// tablet
			onTabletNav(place, historyHandler);

		}

	}

	@Override
	public HandlerRegistration bind(EventBus eventBus,
			final HistoryHandler historyHandler) {
		/*
		 * HandlerRegistration addHandler =
		 * eventBus.addHandler(AnimationSelectedEvent.getType(), new
		 * AnimationSelectedEvent.Handler() {
		 * 
		 * @Override public void onAnimationSelected(AnimationSelectedEvent
		 * event) {
		 * 
		 * Animation animation = event.getAnimation();
		 * 
		 * AnimationNames animationName = animation.getAnimationName();
		 * 
		 * Place place = null;
		 * 
		 * switch (animationName) { case SLIDE: place = new
		 * AnimationSlidePlace();
		 * 
		 * break; case SLIDE_UP: place = new AnimationSlideUpPlace();
		 * 
		 * break; case DISSOLVE: place = new AnimationDissolvePlace();
		 * 
		 * break; case FADE: place = new AnimationFadePlace();
		 * 
		 * break; case FLIP: place = new AnimationFlipPlace();
		 * 
		 * break; case POP: place = new AnimationPopPlace();
		 * 
		 * break; case SWAP: place = new AnimationSwapPlace();
		 * 
		 * break;
		 * 
		 * default: // TODO log place = new AnimationSlidePlace(); break; }
		 * 
		 * if (MGWT.getOsDetection().isTablet()) {
		 * 
		 * historyHandler.replaceCurrentPlace(place); historyHandler.goTo(place,
		 * true); } else { historyHandler.goTo(place); }
		 * 
		 * } });
		 */
		/*
		 * HandlerRegistration register3 =
		 * UIEntrySelectedEvent.register(eventBus, new
		 * UIEntrySelectedEvent.Handler() {
		 * 
		 * @Override public void onAnimationSelected(UIEntrySelectedEvent event)
		 * {
		 * 
		 * UIEntry entry = event.getEntry();
		 * 
		 * Place place = null;
		 * 
		 * switch (entry) { case BUTTON_BAR: place = new ButtonBarPlace();
		 * break; case BUTTONS: place = new ButtonPlace(); break; case ELEMENTS:
		 * place = new ElementsPlace(); break; case POPUPS: place = new
		 * PopupPlace(); break; case PROGRESS_BAR: place = new
		 * ProgressBarPlace(); break; case PROGRESS_INDICATOR: place = new
		 * ProgressIndicatorPlace(); break; case PULL_TO_REFRESH: place = new
		 * PullToRefreshPlace(); break; case SCROLL_WIDGET: place = new
		 * ScrollWidgetPlace(); break; case SEARCH_BOX: place = new
		 * SearchBoxPlace(); break; case SLIDER: place = new SliderPlace();
		 * break; case TABBAR: place = new TabBarPlace(); break;
		 * 
		 * default: break; }
		 * 
		 * if (MGWT.getOsDetection().isTablet()) {
		 * 
		 * historyHandler.replaceCurrentPlace(place); historyHandler.goTo(place,
		 * true); } else { historyHandler.goTo(place); }
		 * 
		 * } });
		 */
		HandlerRegistration register2 = ActionEvent.register(eventBus,
				ActionNames.BACK, new ActionEvent.Handler() {

					@Override
					public void onAction(ActionEvent event) {

						History.back();

					}
				});
/*
		HandlerRegistration register = ActionEvent.register(eventBus,
				ActionNames.ANIMATION_END, new ActionEvent.Handler() {

					@Override
					public void onAction(ActionEvent event) {
						if (MGWT.getOsDetection().isPhone()) {
							History.back();
						} else {
							historyHandler.goTo(new AnimationPlace(), true);
						}

					}
				});
*/
		HandlerRegistrationCollection col = new HandlerRegistrationCollection();
//		col.addHandlerRegistration(register);
		col.addHandlerRegistration(register2);
		// col.addHandlerRegistration(register3);
		// col.addHandlerRegistration(addHandler);
		return col;
	}

	private void onTabletNav(Place place, HistoryHandler historyHandler) {
		if (place instanceof AboutPlace) {
			historyHandler.replaceCurrentPlace(new HomePlace());

		}
	}

}
