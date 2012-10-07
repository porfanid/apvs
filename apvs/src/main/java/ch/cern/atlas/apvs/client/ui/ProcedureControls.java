package ch.cern.atlas.apvs.client.ui;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.NavigateStepEvent;
import ch.cern.atlas.apvs.client.event.NavigateStepEvent.Navigation;
import ch.cern.atlas.apvs.client.event.StepStatusEvent;
import ch.cern.atlas.apvs.client.widget.HorizontalFlowPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;

public class ProcedureControls extends HorizontalFlowPanel implements Module {

	Button start = new Button("Start");
	Button previous = new Button("Previous");
	Button next = new Button("Next");
	Label step = new Label("-");

	// Note only a local event bus
	public ProcedureControls() {
	}

	public void configure(String id, ClientFactory clientFactory, Arguments args) {

		final EventBus eventBus = clientFactory.getEventBus(args.getArg(0));
		
		add(start);
		start.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new NavigateStepEvent(Navigation.START));
			}
		});
		add(previous);
		previous.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new NavigateStepEvent(Navigation.PREVIOUS));
			}
		});
		add(next);
		next.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new NavigateStepEvent(Navigation.NEXT));
			}
		});
		
		add(step);

		StepStatusEvent.register(eventBus, new StepStatusEvent.Handler() {
			@Override
			public void onStepStatus(StepStatusEvent event) {
				previous.setEnabled(event.hasPrevious());
				next.setEnabled(event.hasNext());
				step.setText(event.getStep()+" of "+event.getTotal());
			}
		});
		
		RootPanel.get(id).add(this);
	}
}
