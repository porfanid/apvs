package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.widget.HorizontalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;

public class ClientView extends HorizontalFlowPanel {

	public ClientView(RemoteEventBus eventBus) {
		Label nameLabel = new Label("Name: ");
		add(nameLabel);
		
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		oracle.add("Dimi");
		oracle.add("Mark");
		oracle.add("Marzio");
		oracle.add("Michel");
		oracle.add("Olga");
		oracle.add("Olivier");
		
		SuggestBox name = new SuggestBox(oracle);
		name.setText("Type name...");
		add(name);
		
		Label clientLabel = new Label("Client ID: ");
		add(clientLabel);
		
		ClientSelector clientSelector = new ClientSelector(eventBus);
		add(clientSelector);
		
		Label ptuLabel = new Label("PTU ID: ");
		add(ptuLabel);
		
		Label ptu = new Label("...");
		add(ptu);
	}

}
