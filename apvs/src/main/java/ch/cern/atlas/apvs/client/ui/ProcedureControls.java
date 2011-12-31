package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.client.event.NavigateStepEvent;
import ch.cern.atlas.apvs.client.event.NavigateStepEvent.Navigation;
import ch.cern.atlas.apvs.client.event.StepStatusEvent;
import ch.cern.atlas.apvs.client.widget.HorizontalFlowPanel;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;

public class ProcedureControls extends HorizontalFlowPanel {

	Button start = new Button("Start");
	Button previous = new Button("Previous");
	Button next = new Button("Next");
	Label step = new Label("-");

	// Note only a local event bus
	public ProcedureControls(final RemoteEventBus localEventBus) {
		add(start);
		start.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				localEventBus.fireEvent(new NavigateStepEvent(Navigation.START));
			}
		});
		add(previous);
		previous.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				localEventBus.fireEvent(new NavigateStepEvent(Navigation.PREVIOUS));
			}
		});
		add(next);
		next.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				localEventBus.fireEvent(new NavigateStepEvent(Navigation.NEXT));
			}
		});
		
		add(step);

		StepStatusEvent.register(localEventBus, new StepStatusEvent.Handler() {
			@Override
			public void onStepStatus(StepStatusEvent event) {
				previous.setEnabled(event.hasPrevious());
				next.setEnabled(event.hasNext());
				step.setText(event.getStep()+" of "+event.getTotal());
			}
		});
	}
}
