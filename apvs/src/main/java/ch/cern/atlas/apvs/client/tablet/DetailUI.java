package ch.cern.atlas.apvs.client.tablet;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;

public interface DetailUI extends IsWidget {

	public HasText getHeader();

	public HasText getBackbuttonText();

	public HasTapHandlers getBackbutton();

	public HasText getMainButtonText();

	public HasTapHandlers getMainButton();
}
