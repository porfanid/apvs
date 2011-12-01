package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class CameraView extends SimplePanel {

	// FIXME
	private final String cameraURL = "rtsp://pcatlaswpss02:8554/worker1";
	private int videoWidth = 640;
	private int videoHeight = 360;
	private String videoPoster = "camera.jpg"; // FIXME
	@SuppressWarnings("unused")
	private RemoteEventBus eventBus;

	public CameraView(RemoteEventBus eventBus) {
		this.eventBus = eventBus;

		String source = cameraURL;
		System.err.println(source);
		setWidget(new HTML("<video width='" + videoWidth + "' height='"
				+ videoHeight + "' poster='" + videoPoster + "' controls autoplay>"
				+ "<source src='" + source + "'></source>" + "</video>"));

	}
}
