package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.NavigateStepEvent;
import ch.cern.atlas.apvs.client.event.SelectStepEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class ProcedureView extends SimplePanel {

	// FIXME
//	private final String procedureURL = "http://localhost:8890/apvs-procs/procedures/Demo";
//	private String procedure = "mural";
	private final String procedureURL = "http://localhost:8890/apvs-procs/procedures/Tile";
//	private final String procedureURL = "http://pc-atlas-duns:8890/apvs-procs/procedures/Tile";
	private String procedure = "TileDrawerExtraction";
	
	private int step = 1;
	private String extension = ".m4v";
	private String videoType = "video/x-m4v"; // video/mp4
	private int videoWidth = 350; // 640;
	private int videoHeight = 300;
	private String videoPoster = "procedure.jpg"; // FIXME
	// FIXME...
	private final int firstStep = 1;
	private final int lastStep = 34;
	private RemoteEventBus eventBus;

	public ProcedureView(RemoteEventBus eventBus) {
		this(eventBus, 350, 300);
	}
	
	public ProcedureView(RemoteEventBus eventBus, int width, int height) {
		this.eventBus = eventBus;
		this.videoWidth = width;
		this.videoHeight = height;
		
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
				Video video = Video.createIfSupported();
				video.setWidth(videoWidth+Unit.PX.toString());
				video.setHeight(videoHeight+Unit.PX.toString());
				video.setPoster(videoPoster);
				video.setAutoplay(true);
				video.setControls(true);
				video.setLoop(true);
				video.setMuted(true);
				video.addSource(source, videoType);
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
