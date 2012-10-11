package ch.cern.atlas.apvs.client.tablet;

import java.util.List;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

public interface MainMenuUI extends IsWidget {

	public void setTitle(String text);

	public void setRightButtonText(String text);

	public HasTapHandlers getAboutButton();

	public HasCellSelectedHandler getCellSelectedHandler();

	public void setTopics(List<Topic> createTopicsList);

	public HasText getFirstHeader();

}
