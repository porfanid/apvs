package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.NavigateStepEvent;
import ch.cern.atlas.apvs.client.event.SelectStepEvent;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

public class ProcedureView extends SimplePanel {

	// FIXME
	private final String procedureURL = "http://localhost:8890/apvs-procs";
	private String procedure = "mural-m4v";
	private int step = 1;
	private String extension = ".m4v";
	private String contentType = "video/x-m4v"; // video/mp4
	private int videoWidth = 640;
	private int videoHeight = 360;
	private String videoPoster = "poster.jpg"; // FIXME
	// FIXME...
	private final int firstStep = 1;
	private final int lastStep = 6;
	private EventBus eventBus;

	public ProcedureView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		NavigateStepEvent.register(eventBus, new NavigateStepEvent.Handler() {

			@Override
			public void onNavigateStep(NavigateStepEvent event) {
				switch (event.getNavigation()) {
				case START:
					start();
					break;
				case PREVIOUS:
					previous();
					break;
				case NEXT:
					next();
					break;
				default:
					break;
				}
			}
		});
		
		SelectStepEvent.register(eventBus, new SelectStepEvent.Handler() {
			
			@Override
			public void onStepSelected(SelectStepEvent event) {
				String source = procedureURL + "/" + procedure + "/" + step
						+ extension;
				System.err.println(source);
				setWidget(new HTML(
						"<video width='"+videoWidth+"' height='"+videoHeight+"' poster='"+videoPoster+"' controls autoplay>"
								+ "<source src='" + source
								+ "' type='"+contentType+"'></source>" + "</video>"));

			}
		});
	}

	public void setStep(int step) {
		if ((firstStep <= step) && (step <= lastStep)) {
			this.step = step;
			eventBus.fireEvent(new SelectStepEvent(step, lastStep, hasPrevious(), hasNext()));
		}
	}

	public void start() {
		setStep(firstStep);
	}

	public void end() {
		setStep(lastStep);
	}

	public boolean hasNext() {
		return step < lastStep;
	}

	public void next() {
		if (hasNext()) {
			setStep(++step);
		}
	}

	public boolean hasPrevious() {
		return step > firstStep;
	}

	public void previous() {
		if (hasPrevious()) {
			setStep(--step);
		}
	}
}
