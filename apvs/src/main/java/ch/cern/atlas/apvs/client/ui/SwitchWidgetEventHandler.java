package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.client.event.SwitchWidgetEvent;
import ch.cern.atlas.apvs.client.widget.IsSwitchableWidget;

import com.google.gwt.dom.client.Element;
import com.google.web.bindery.event.shared.EventBus;

public class SwitchWidgetEventHandler implements SwitchWidgetEvent.Handler {
	
//	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private EventBus switchBus;
	private Element element;
	private IsSwitchableWidget currentWidget;

	public SwitchWidgetEventHandler(EventBus switchBus, Element element, IsSwitchableWidget switchableWidget) {
		this.switchBus = switchBus;
		this.element = element;
		this.currentWidget = switchableWidget;
	}

	@Override
	public void onSwitchWidget(SwitchWidgetEvent event) {
		String title = event.getTitle();
		IsSwitchableWidget widget = event.getSwitchableWidget();
		boolean replacement = event.isReplacement();

		IsSwitchableWidget oldWidget = currentWidget;
		boolean isDestination = oldWidget.isDestination();
		if (replacement) {
			if (!isDestination && (element.getChildCount() == 0)) {
//				log.info("Received other window in switch " + title);
				switchToWidget(title, widget);
			}
		} else if (isDestination) {
			String oldTitle = element.getParentElement().getChild(1)
					.getChild(0).getNodeValue();
			
//			log.info("Received switch " + title);
			// Widget is auto-removed from source
			switchToWidget(title, widget);

			// send my own window back
			SwitchWidgetEvent.fire(switchBus, oldTitle, oldWidget, true);
		}
	}

	private void switchToWidget(String title, IsSwitchableWidget switchableWidget) {
		switchableWidget.toggleDestination();
		element.getParentElement().getChild(1).getChild(0).setNodeValue(title+" "+switchableWidget.isDestination());
		element.appendChild(switchableWidget.asWidget().getElement());
		currentWidget = switchableWidget;
	}

}
