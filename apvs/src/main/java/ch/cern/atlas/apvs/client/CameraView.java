package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.SelectPtuEvent;
import ch.cern.atlas.apvs.client.event.SettingsChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

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
		setWidget(new HTML("<img width=\""+videoWidth+"\" height=\""+videoHeight+"\" src=\""+videoPoster+"\"/>"));
		
		if (ptuId == null) return;
		
		String cameraUrl = getCameraUrl(type, ptuId);
		if (cameraUrl == null) return;
		
		HTML html;
		if (cameraUrl.startsWith("http://")) {
			html = new HTML("<video width='" + videoWidth + "' height='"
					+ videoHeight + "' poster='" + videoPoster
					+ "' controls autoplay loop>" + "<source src='" + cameraUrl
					+ "'></source>" + "</video>");
		} else {
			html = new HTML(quickTime + 
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
		}
		System.err.println(html.toString());
		setWidget(html);
	}
}
