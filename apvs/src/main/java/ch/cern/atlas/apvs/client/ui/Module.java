package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.client.ClientFactory;

import com.google.gwt.dom.client.Element;

public interface Module {

	/**
	 * return true to add the element to the id'ed rootpanel
	 * 
	 * @param element
	 * @param clientFactory
	 * @param args
	 * @return
	 */
	public boolean configure(Element element, ClientFactory clientFactory, Arguments args);
}
