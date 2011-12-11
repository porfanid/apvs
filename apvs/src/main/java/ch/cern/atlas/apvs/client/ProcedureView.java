package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.NavigateStepEvent;
import ch.cern.atlas.apvs.client.event.SelectStepEvent;
import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
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
	private int step = 1;
	private String extension = ".m4v";
	private String videoType = "video/x-m4v"; // video/mp4
	private int videoWidth = 350; // 640;
	private int videoHeight = 300;
	private String videoPoster = "Default-640x480.jpg";
	// FIXME...
	private final int firstStep = 1;
	private final int lastStep = 34;
	private RemoteEventBus remoteEventBus;

	public ProcedureView(RemoteEventBus remoteEventBus, RemoteEventBus localEventBus) {
		this(remoteEventBus, localEventBus, 350, 300);
	}

	public ProcedureView(final RemoteEventBus remoteEventBus, RemoteEventBus localEventBus, int width, int height) {
		this.remoteEventBus = remoteEventBus;
		this.videoWidth = width;
		this.videoHeight = height;

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
				System.err.println("PV: ss changed "+event.getServerSettings().get(ServerSettings.settingNames[2]));
				procedureURL = event.getServerSettings().get(ServerSettings.settingNames[2]);
				
				update();
			}
		});

		SelectStepEvent.register(remoteEventBus, new SelectStepEvent.Handler() {

			@Override
			public void onStepSelected(SelectStepEvent event) {
				System.err.println("PV: "+event+" "+event.getEventBusUUID()+" "+remoteEventBus.getUUID());
				step = event.getStep();
				
				update();
			}
		});
	}
	
	private void update() {
		String source = procedureURL + "/" + procedure + "/" + step
				+ extension;
		Video video = Video.createIfSupported();
		video.setWidth(videoWidth + Unit.PX.toString());
		video.setHeight(videoHeight + Unit.PX.toString());
		video.setPoster(videoPoster);
		video.setAutoplay(true);
		video.setControls(true);
		video.setLoop(true);
		video.setMuted(true);
		video.addSource(source, videoType);
		setWidget(video);
			
		System.err.println(source);
		
	}

	public void setStep(int step) {
		if ((firstStep <= step) && (step <= lastStep)) {
			remoteEventBus.fireEvent(new SelectStepEvent(step, lastStep,
					hasPrevious(), hasNext()));
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
			setStep(step+1);
		}
	}

	public boolean hasPrevious() {
		return step > firstStep;
	}

	public void previous() {
		if (hasPrevious()) {
			setStep(step-1);
		}
	}
}
