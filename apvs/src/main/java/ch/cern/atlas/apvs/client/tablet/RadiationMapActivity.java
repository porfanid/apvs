package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

public class RadiationMapActivity extends DetailActivity {

	private final ClientFactory clientFactory;

	public RadiationMapActivity(ClientFactory clientFactory) {
		super(clientFactory.getRadiationMapView(), "nav");
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(AcceptsOneWidget panel, final EventBus eventBus) {
		super.start(panel, eventBus);
		
		RadiationMapView view = clientFactory.getRadiationMapView();

		view.getBackbuttonText().setText("Home");

		view.getHeader().setText("Radiation Map");

		view.getMainButtonText().setText("Nav");

		addHandlerRegistration(view.getBackbutton().addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				ActionEvent.fire(eventBus, ActionNames.BACK);
			}
		}));

		panel.setWidget(view);
	}
}
