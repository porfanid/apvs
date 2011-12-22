package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.NavigateStepEvent;
import ch.cern.atlas.apvs.client.event.SelectStepEvent;
import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.StepStatusEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.SimplePanel;

public class ProcedureView extends SimplePanel {

	// FIXME
	// private final String procedureURL =
	// "http://localhost:8890/apvs-procs/procedures/Demo";
	// private String procedure = "mural";
	// private final String procedureURL =
	// "http://pc-atlas-duns:8890/apvs-procs/procedures/Tile";
	private String procedure = "TileDrawerExtraction";

	private String procedureURL = "";
	private int step;
	private String extension = ".m4v";
	private String videoType = "video/x-m4v"; // video/mp4
	private int videoWidth = 350; // 640;
	private int videoHeight = 300;
//	private String videoPoster = "Default-640x480.jpg";
	// FIXME...
	private final int firstStep = 1;
	private final int lastStep = 34;
	private RemoteEventBus remoteEventBus;
	private RemoteEventBus localEventBus;

	private Object oldSource;

	public ProcedureView(RemoteEventBus remoteEventBus, RemoteEventBus localEventBus) {
		this(remoteEventBus, localEventBus, 350, 300);
	}

	public ProcedureView(final RemoteEventBus remoteEventBus, final RemoteEventBus localEventBus, int width, int height) {
		this(remoteEventBus, localEventBus, width, height, "FIXME", "FIXME", Integer.toString(1));
	}
	
	public ProcedureView(RemoteEventBus remoteEventBus,
			final RemoteEventBus localEventBus, int width, int height, String url,
			String name, String step) {

		this.remoteEventBus = remoteEventBus;
		this.localEventBus = localEventBus;
		this.videoWidth = width;
		this.videoHeight = height;
		
		this.step = Integer.parseInt(step);

		NavigateStepEvent.register(localEventBus, new NavigateStepEvent.Handler() {

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
		
		ServerSettingsChangedEvent.subscribe(remoteEventBus, new ServerSettingsChangedEvent.Handler() {
			
			@Override
			public void onServerSettingsChanged(ServerSettingsChangedEvent event) {
				procedureURL = event.getServerSettings().get(ServerSettings.settingNames[2]);
				localEventBus.fireEvent(new StepStatusEvent(ProcedureView.this.step, lastStep, hasPrevious(), hasNext()));
				update();
			}
		});
		
		update();
}

	private void update() {
		String source = procedureURL + "/" + procedure + "/" + step
				+ extension;
		if (source.equals(oldSource)) return;
		oldSource = source;
		
		Video video = Video.createIfSupported();
		video.setWidth(videoWidth + Unit.PX.toString());
		video.setHeight(videoHeight + Unit.PX.toString());
// Annoying
// 		video.setPoster(videoPoster);
		video.setAutoplay(true);
		video.setControls(true);
		video.setLoop(true);
		video.setMuted(true);
		video.addSource(source, videoType);
		setWidget(video);
			
		System.err.println(source);
//		Thread.dumpStack();	
	}

	private void navigateStep(int step) {
		if ((firstStep <= step) && (step <= lastStep)) {
			this.step = step;
			remoteEventBus.fireEvent(new SelectStepEvent(step));
			localEventBus.fireEvent(new StepStatusEvent(step, lastStep, hasPrevious(), hasNext()));
			update();
		}
	}

	public void start() {
		navigateStep(firstStep);
	}

	public void end() {
		navigateStep(lastStep);
	}

	public boolean hasNext() {
		return step < lastStep;
	}

	public void next() {
		if (hasNext()) {
			navigateStep(step+1);
		}
	}

	public boolean hasPrevious() {
		return step > firstStep;
	}

	public void previous() {
		if (hasPrevious()) {
			navigateStep(step-1);
		}
	}
}
