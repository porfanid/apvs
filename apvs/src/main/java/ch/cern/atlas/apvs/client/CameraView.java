package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.client.event.SettingsChangedEvent;
import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class CameraView extends SimplePanel {

	// FIXME
//	private final String cameraURL = "rtsp://pcatlaswpss02:8554/worker1";
//	private final String cameraURL = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
	// private final String cameraURL = "http://devimages.apple.com/iphone/samples/bipbop/gear4/prog_index.m3u8";
	private int videoWidth = 480;
	private int videoHeight = 360;
	private String videoPoster = "Default-640x480.jpg";

	private String urlName;

	private Settings settings;
	
	private final static String quickTime = "<script type=\"text/javascript\" language=\"javascript\" src=\"quicktime/AC_QuickTime.js\"></script>";

	public CameraView(RemoteEventBus eventBus, String urlName) {
		this.urlName = urlName;
		
		SettingsChangedEvent.subscribe(eventBus, new SettingsChangedEvent.Handler() {
			
			@Override
			public void onSettingsChanged(SettingsChangedEvent event) {
				settings = event.getSettings();
				
				update();
			}
		});
	}
	
	private void update() {
		// FIXME, person fixed to be person 0;
		String cameraUrl = settings.get(0, urlName);
		if (cameraUrl == null) return;
		
		HTML html;
		if (cameraUrl.startsWith("http://")) {
			html = new HTML("<video width='" + videoWidth + "' height='"
					+ videoHeight + "' poster='" + videoPoster
					+ "' controls autoplay>" + "<source src='" + cameraUrl
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
