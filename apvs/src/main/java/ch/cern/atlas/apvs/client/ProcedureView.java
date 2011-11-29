package ch.cern.atlas.apvs.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

public class ProcedureView extends SimplePanel {

	private final String procedureURL = "http://localhost:8890/apvs-proc";
	private String procedure = "mural-m4v";
	private int step = 1;
	private String extension = ".m4v";
	// FIXME...
	private final int firstStep = 1;
	private final int lastStep = 6;

	public ProcedureView(EventBus eventBus) {
		setStep(firstStep);
		NavigateStepEvent.register(eventBus, new NavigateStepEvent.Handler() {

			@Override
			public void onStepSelect(NavigateStepEvent event) {
				switch (event.getSelection()) {
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
	}

	public void setStep(int step) {
		if ((firstStep <= step) && (step <= lastStep)) {
			this.step = step;
			String source = procedureURL + "/" + procedure + "/" + step
					+ extension;
			System.err.println(source);
			setWidget(new HTML(
					"<video width='640' height='360' poster='poster.jpg' controls autoplay>"
							+ "<source src='" + source
							+ "' type='video/mp4'></source>" + "</video>"));
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
