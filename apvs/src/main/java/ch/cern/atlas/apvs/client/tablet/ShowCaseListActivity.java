package ch.cern.atlas.apvs.client.tablet;

import java.util.ArrayList;
import java.util.List;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

public class ShowCaseListActivity extends MGWTAbstractActivity {

	private final ClientFactory clientFactory;

	public ShowCaseListActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;

	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		ShowCaseListView view = clientFactory.getHomeView();

		view.setTitle("APVS");
		view.setRightButtonText("about");

		view.getFirstHeader().setText("Home");

		view.setTopics(createTopicsList());

		addHandlerRegistration(view.getCellSelectedHandler().addCellSelectedHandler(new CellSelectedHandler() {

			@Override
			public void onCellSelected(CellSelectedEvent event) {
				int index = event.getIndex();
				System.err.println(index);
				switch(index) {
				case 2:
					clientFactory.getPlaceController().goTo(new ModelPlace());
				case 3:
					clientFactory.getPlaceController().goTo(new RadiationMapPlace());
					return;
				}
			}
		}));

		addHandlerRegistration(view.getAboutButton().addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				clientFactory.getPlaceController().goTo(new AboutPlace());
			}
		}));

		panel.setWidget(view);
	}

	private List<Topic> createTopicsList() {
		ArrayList<Topic> list = new ArrayList<Topic>();
		list.add(new Topic("Procedures", 5));
		list.add(new Topic("Camera", 5));
		list.add(new Topic("2D/3D Models", 5));
		list.add(new Topic("Radiation Map", 5));

		return list;
	}

}
