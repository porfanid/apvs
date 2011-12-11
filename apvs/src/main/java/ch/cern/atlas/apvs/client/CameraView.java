package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.event.SettingsChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CameraView extends SimplePanel {

	public static final int HELMET = 0;
	public static final int HAND = 1;
	
	// FIXME
//	private final String cameraURL = "rtsp://pcatlaswpss02:8554/worker1";
//	private final String cameraURL = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
	// private final String cameraURL = "http://devimages.apple.com/iphone/samples/bipbop/gear4/prog_index.m3u8";
//	private final String cameraURL = "http://quicktime.tc.columbia.edu/users/lrf10/movies/sixties.mov";
//	private final String cameraURL = "rtsp://quicktime.tc.columbia.edu:554/users/lrf10/movies/sixties.mov";
	private int videoWidth = 400;
	private int videoHeight = 300;
	private String videoPoster = "Default-640x480.jpg";

	private int type;

	private Settings settings;
	private Integer ptuId;
	
	private final static String quickTime = "<script type=\"text/javascript\" language=\"javascript\" src=\"quicktime/AC_QuickTime.js\"></script>";

	public CameraView(RemoteEventBus remoteEventBus, RemoteEventBus localEventBus, int type) {
		this.type = type;
		
		SettingsChangedEvent.subscribe(remoteEventBus, new SettingsChangedEvent.Handler() {
			
			@Override
			public void onSettingsChanged(SettingsChangedEvent event) {
				settings = event.getSettings();
				
				update();
			}
		});
		
		SelectPtuEvent.subscribe(localEventBus, new SelectPtuEvent.Handler() {
			
			@Override
			public void onPtuSelected(SelectPtuEvent event) {
				ptuId = event.getPtuId();
				update();
			}
		});
	}
	
	private String getCameraUrl(int type, int ptuId) {
		return type == HELMET ? settings.getHelmetCameraUrl(ptuId) : settings.getHandCameraUrl(ptuId);
	}

	private void update() {
		Image image = new Image(videoPoster);
		image.setWidth(videoWidth+Unit.PX.toString());
		image.setHeight(videoHeight+Unit.PX.toString());
		setWidget(image);
						
		if (ptuId == null) return;
		
		String cameraUrl = getCameraUrl(type, ptuId);
		if (cameraUrl == null) return;
		
		if (cameraUrl.startsWith("http://")) {
			Video video = Video.createIfSupported();
			if (video != null) {
				video.setWidth(videoWidth+Unit.PX.toString());
				video.setHeight(videoHeight+Unit.PX.toString());
				video.setControls(true);
				video.setAutoplay(true);
				video.setPoster(videoPoster);
				video.setMuted(true);
				video.setLoop(true);
				video.addSource(cameraUrl);
			}
			System.err.println(video.toString());
			setWidget(video);
		} else {
			Widget video = new HTML(quickTime + 
					"<script language=\"javascript\" type=\"text/javascript\">"
							+ "QT_WriteOBJECT('"
							+ videoPoster
							+ "', '"
							+ videoWidth
							+ "', '"
							+ videoHeight
							+ "', '', 'href', '"
							+ cameraUrl
							+ "', 'autohref', 'true', 'target', 'myself', 'controller', 'true', 'autoplay', 'true');</script>");
			System.err.println(video.toString());
			setWidget(video);
		}
	}
}
