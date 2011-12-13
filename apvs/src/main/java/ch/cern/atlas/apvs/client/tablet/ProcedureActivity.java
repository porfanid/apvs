package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

public class ProcedureActivity extends DetailActivity {

	private final ClientFactory clientFactory;
	private String url;
	private String name;
	private String step;
	
	public ProcedureActivity(ClientFactory clientFactory, String url, String name, String step) {
		super(clientFactory.getProcedurePanel(url, name, step), "nav");
		this.clientFactory = clientFactory;
		this.url = url;
		this.name = name;
		this.step = step;
	}

	@Override
	public void start(AcceptsOneWidget panel, final EventBus eventBus) {
		super.start(panel, eventBus);
		
		ProcedureUI view = clientFactory.getProcedurePanel(url, name, step);

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
