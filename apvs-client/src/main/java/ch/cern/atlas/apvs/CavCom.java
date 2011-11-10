package ch.cern.atlas.apvs;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CavCom implements EntryPoint {

	private static final int REFRESH_INTERVAL = 3000;

	private static final String JSON_URL = "http://atlas.web.cern.ch/Atlas/TCOORD/CavCom/readmon.php";
	private static final String CAMERA_URL = "http://atlas.web.cern.ch/Atlas/TCOORD/CavCom/camera-stream.php";

	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable radFlexTable = new FlexTable();
	private HashMap<String, Integer> rowIndex = new HashMap<String, Integer>();
	private Label label = new Label();

	private Label lbl = new Label();
	private VerticalPanel verticalPanel = new VerticalPanel();
	private HorizontalPanel horizontalPanel = new HorizontalPanel();
	private boolean vertical;
	private Image hatImage = new Image();
	private Image manualImage = new Image();
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// Point the image at a real URL.
		hatImage.setUrlAndVisibleRect(CAMERA_URL, 0, 0, 640, 480);
		manualImage.setUrlAndVisibleRect(CAMERA_URL, 640, 0, 640, 480);

		// Add the image, label, and clip/restore buttons to the root panel.
		verticalPanel.add(hatImage);
		verticalPanel.add(manualImage);
		vertical = true;
		
		mainPanel.add(verticalPanel);
		
		setOrientation();

		radFlexTable.setText(0, 0, "SerialNo");
		radFlexTable.setHTML(0, 1, "Dose (&micro;Sv)");
		radFlexTable.setHTML(0, 2, "Rate (&micro;Sv/h)");
		radFlexTable.setText(0, 3, "Update");

		radFlexTable.getRowFormatter().addStyleName(0, "radListHeader");
		radFlexTable.addStyleName("radList");

		// mainPanel.add(radFlexTable);

		label.setText("None");
		// mainPanel.add(label);

		RootPanel.get("radList").add(mainPanel);

		Window.addWindowResizeListener(new WindowResizeListener() {
			public void onWindowResized(int width, int height) {
				setOrientation();
			}
		});

		// Setup timer to refresh list automatically.
		Timer refreshTimer = new Timer() {
			@Override
			public void run() {
				updateList();
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
	}
	
	private static int orientationThreshold = 10;
	
	private void setOrientation() {
		if (vertical) {
			if (Window.getClientWidth() > Window.getClientHeight() + orientationThreshold) {
				mainPanel.remove(verticalPanel);
				verticalPanel.clear();
				
				horizontalPanel.add(hatImage);
				horizontalPanel.add(manualImage);
				mainPanel.add(horizontalPanel);
				vertical = false;
			}
		} else {
			if (Window.getClientHeight() > Window.getClientWidth() + orientationThreshold) {
				mainPanel.remove(horizontalPanel);
				horizontalPanel.clear();
				
				verticalPanel.add(hatImage);
				verticalPanel.add(manualImage);
				mainPanel.add(verticalPanel);
				vertical = true;
			}			
		}		
	}

	/**
	 * Send the name from the nameField to the server and wait for a response.
	 */
	private void updateList() {

		final String url = URL.encode(JSON_URL);

		// Send request to server and catch any errors.
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try {
			Request request = builder.sendRequest(null, new RequestCallback() {

				public void onError(Request request, Throwable exception) {
					Window.alert("Couldn't retrieve JSON " + url);
				}

				public void onResponseReceived(Request request,
						Response response) {
					if (200 == response.getStatusCode()) {
						JSONObject entries = (JSONObject) JSONParser
								.parseStrict(response.getText());

						for (Iterator<String> i = entries.keySet().iterator(); i
								.hasNext();) {
							String serialNo = i.next();
							if (!rowIndex.containsKey(serialNo)) {
								rowIndex.put(serialNo,
										radFlexTable.getRowCount());
							}
							int row = rowIndex.get(serialNo);
							radFlexTable.setText(row, 0, serialNo);
							radFlexTable.getCellFormatter().addStyleName(row,
									0, "radListNumericColumn");
							radFlexTable.getCellFormatter().addStyleName(row,
									1, "radListNumericColumn");
							radFlexTable.getCellFormatter().addStyleName(row,
									2, "radListNumericColumn");

							JSONObject entry = (JSONObject) entries
									.get(serialNo);
							radFlexTable.setText(row, 1,
									((JSONNumber) entry.get("dose")).toString());
							radFlexTable.setText(row, 2,
									((JSONNumber) entry.get("rate")).toString());
							radFlexTable.setText(row, 3, ((JSONString) entry
									.get("update")).stringValue());
						}

					} else {
						Window.alert("Couldn't retrieve JSON ("
								+ response.getStatusCode() + " "
								+ response.getStatusText() + " " + url + ")");
					}
				}
			});
		} catch (RequestException e) {
			Window.alert("Couldn't retrieve JSON");
		}

	}
}
