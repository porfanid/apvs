package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

public class ImageActivity extends DetailActivity {

	private final ClientFactory clientFactory;
	private String name;
	private String url;

	public ImageActivity(ClientFactory clientFactory, String name, String url) {
		super(clientFactory.getImagePanel(url), "nav");
		this.clientFactory = clientFactory;
		this.name = name;
		this.url = url;
	}

	@Override
	public void start(AcceptsOneWidget panel, final EventBus eventBus) {
		super.start(panel, eventBus);
		
		ImageUI view = clientFactory.getImagePanel(url);

		view.getBackbuttonText().setText("Home");

		view.getHeader().setText(name);

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
