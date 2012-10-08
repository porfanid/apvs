package ch.cern.atlas.apvs.client.tablet;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

public class MainMenuActivity extends MGWTAbstractActivity {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private final ClientFactory clientFactory;

	public MainMenuActivity(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;

	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		MainMenuUI view = clientFactory.getHomeView();

		view.setTitle("Menu");
		view.setRightButtonText("about");

		view.getFirstHeader().setText("");

		view.setTopics(createTopicsList());

		addHandlerRegistration(view.getCellSelectedHandler().addCellSelectedHandler(new CellSelectedHandler() {

			@Override
			public void onCellSelected(CellSelectedEvent event) {
				int index = event.getIndex();
				log.info(""+index);
				switch(index) {
				case 0:
					clientFactory.getPlaceController().goTo(new ProcedureMenuPlace());
					break;
				case 1:
					clientFactory.getPlaceController().goTo(new CameraPlace("Helmet"));
					break;
				case 2:
					clientFactory.getPlaceController().goTo(new ModelPlace());
					break;
				case 3:
					clientFactory.getPlaceController().goTo(new ImagePlace("Radiation Map", "images/InnerGapDosesMap.png"));
					break;
				}
			}
		}));

		addHandlerRegistration(view.getAboutButton().addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				clientFactory.getPlaceController().goTo(new HomePlace());
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
