package ch.cern.atlas.apvs.client.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.client.ClientFactory;
import ch.cern.atlas.apvs.client.event.NavigateStepEvent;
import ch.cern.atlas.apvs.client.event.SelectStepEvent;
import ch.cern.atlas.apvs.client.event.ServerSettingsChangedEvent;
import ch.cern.atlas.apvs.client.event.StepStatusEvent;
import ch.cern.atlas.apvs.client.settings.ServerSettings;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

public class ProcedureView extends SimplePanel {

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
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
	private String videoWidth = 350+Unit.PX.toString(); // 640;
	private String videoHeight = 300+Unit.PX.toString();
//	private String videoPoster = "Default-640x480.jpg";
	// FIXME...
	private final int firstStep = 1;
	private final int lastStep = 34;
	private RemoteEventBus remoteEventBus;
	private EventBus localEventBus;

	private Object oldSource;

	public ProcedureView(ClientFactory factory, Arguments args) {
		this(factory, args, "100%", "100%");
	}

	public ProcedureView(ClientFactory factory, Arguments args, String width, String height) {
		this(factory, args, width, height, "FIXME", "FIXME", Integer.toString(1));
	}
	
	public ProcedureView(ClientFactory factory, Arguments args, String width, String height, String url,
			String name, String step) {

		this.remoteEventBus = factory.getRemoteEventBus();
		this.localEventBus = factory.getEventBus(args.getArg(0));
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
				procedureURL = event.getServerSettings().get(ServerSettings.Entry.procedureUrl.toString());
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
		video.setWidth(videoWidth);
		video.setHeight(videoHeight);
// Annoying
// 		video.setPoster(videoPoster);
		video.setAutoplay(true);
		video.setControls(true);
		video.setLoop(true);
		video.setMuted(true);
		video.addSource(source, videoType);
		setWidget(video);
			
		log.info(source);
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
