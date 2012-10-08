package ch.cern.atlas.apvs.client.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.event.SwitchWidgetEvent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class SwitchWidgetEventHandler implements SwitchWidgetEvent.Handler {
	
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private EventBus switchBus;
	private Element element;
	private Widget currentWidget;
	private boolean isDestination;

	public SwitchWidgetEventHandler(EventBus switchBus, Element element, Widget widget, boolean isDestination) {
		this.switchBus = switchBus;
		this.element = element;
		this.currentWidget = widget;
		this.isDestination = isDestination;
	}

	@Override
	public void onSwitchWidget(SwitchWidgetEvent event) {
		String title = event.getTitle();
		Widget widget = event.getWidget();
		boolean replacement = event.isReplacement();

		if (replacement) {
			if (!isDestination && (element.getChildCount() == 0)) {
				log.info("Received other window in switch " + title);
				switchToWidget(title, widget);
			}
		} else if (isDestination) {
			String oldTitle = element.getParentElement().getChild(1)
					.getChild(0).getNodeValue();
			Widget oldWidget = currentWidget;
			widget.removeFromParent();

			log.info("Received switch " + title);
			// Widget is auto-removed from source
			switchToWidget(title, widget);

			// send my own window back
			SwitchWidgetEvent.fire(switchBus, oldTitle, oldWidget, true);
		}
	}

	private void switchToWidget(String title, Widget widget) {
		element.getParentElement().getChild(1).getChild(0).setNodeValue(title);
		element.appendChild(widget.getElement());
		currentWidget = widget;
	}

}
