package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

public class CameraActivity extends DetailActivity {

	private final ClientFactory clientFactory;
	private int type;

	public CameraActivity(ClientFactory clientFactory, int type) {
		super(clientFactory.getCameraView(type), "nav");
		this.clientFactory = clientFactory;
		this.type = type;
	}

	@Override
	public void start(AcceptsOneWidget panel, final EventBus eventBus) {
		super.start(panel, eventBus);
		
		CameraUI view = clientFactory.getCameraView(type);

		view.getBackbuttonText().setText("Home");

		view.getHeader().setText("Camera");

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
