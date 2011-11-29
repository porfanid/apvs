package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.NavigateStepEvent.Navigation;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.EventBus;

public class ProcedureControls extends HorizontalPanel {

	Button start = new Button("Start");
	Button previous = new Button("Previous");
	Button next = new Button("Next");
	Label step = new Label("-");

	public ProcedureControls(final EventBus eventBus) {
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

		SelectStepEvent.register(eventBus, new SelectStepEvent.Handler() {
			@Override
			public void onStepSelected(SelectStepEvent event) {
				previous.setEnabled(event.hasPrevious());
				next.setEnabled(event.hasNext());
				step.setText(event.getStep()+" of "+event.getTotal());
			}
		});
	}
}
