package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.SelectStepEvent.Selection;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;

public class ProcedureControls extends FlowPanel {

	Button start = new Button("Start");
	Button previous = new Button("Previous");
	Button next = new Button("Next");
	
	public ProcedureControls(final EventBus eventBus) {
		add(start);
		start.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new SelectStepEvent(Selection.START));
			}
		});
		add(previous);
		previous.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new SelectStepEvent(Selection.PREVIOUS));
				
			}
		});
		add(next);
		next.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new SelectStepEvent(Selection.NEXT));
			}
		});
	}
}
