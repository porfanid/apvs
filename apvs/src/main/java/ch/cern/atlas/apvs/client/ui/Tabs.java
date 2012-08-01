package ch.cern.atlas.apvs.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;

public class Tabs {

	private static Element currentElement;
	private static Map<String, Element> tabs = new HashMap<String, Element>();

	public static native void setVisible(Element elem, boolean visible) /*-{
																		elem.style.display = visible ? '' : 'none';
																		}-*/;

	public static void add(String id, Element element) {
		setVisible(element, false);
		tabs.put(id, element);
	}

	public static void setCurrentTab(String id) {
		Element element = tabs.get(id);
		if ((element != null) && !element.equals(currentElement)) {
			if (currentElement != null) {
				setVisible(currentElement, false);
			}
			currentElement = element;
			setVisible(currentElement, true);
		}
	}

}
