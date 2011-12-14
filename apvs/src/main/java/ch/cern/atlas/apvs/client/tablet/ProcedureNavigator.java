package ch.cern.atlas.apvs.client.tablet;

import ch.cern.atlas.apvs.client.event.NavigateStepEvent;
import ch.cern.atlas.apvs.client.event.NavigateStepEvent.Navigation;
import ch.cern.atlas.apvs.client.event.StepStatusEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ArrowLeftButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ArrowRightButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBarSpacer;
import com.googlecode.mgwt.ui.client.widget.buttonbar.RewindButton;

public class ProcedureNavigator extends ButtonBar {

	RewindButton start = new RewindButton();
	ArrowLeftButton previous = new ArrowLeftButton();
	ArrowRightButton next = new ArrowRightButton();
	Label step = new Label("-");

	// Note only a local event bus
	public ProcedureNavigator(final RemoteEventBus localEventBus) {
		add(new ButtonBarSpacer());
		add(start);
		start.addTapHandler(new TapHandler() {
			
			@Override
			public void onTap(TapEvent event) {
				localEventBus.fireEvent(new NavigateStepEvent(Navigation.START));
			}
		}); 

		add(new ButtonBarSpacer());

		add(previous);
		previous.addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				localEventBus.fireEvent(new NavigateStepEvent(Navigation.PREVIOUS));
			}
		});

		add(new ButtonBarSpacer());

		add(next);
		next.addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				System.err.println("NEXT");
				localEventBus.fireEvent(new NavigateStepEvent(Navigation.NEXT));
			}
		});

		add(new ButtonBarSpacer());

		add(step);

		add(new ButtonBarSpacer());

		StepStatusEvent.register(localEventBus, new StepStatusEvent.Handler() {
			@Override
			public void onStepStatus(StepStatusEvent event) {
//				previous.setVisible(event.hasPrevious());
//				next.setVisible(event.hasNext());
				step.setText(event.getStep()+" of "+event.getTotal());
			}
		});
	}
}
