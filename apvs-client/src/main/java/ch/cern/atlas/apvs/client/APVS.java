package ch.cern.atlas.apvs.client;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.AtmosphereListener;
import org.atmosphere.gwt.client.extra.Window;
import org.atmosphere.gwt.client.extra.WindowFeatures;
import org.atmosphere.gwt.client.extra.WindowSocket;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Mark Donszelmann
 */
public class APVS implements EntryPoint {

	PollAsync polling = GWT.create(Poll.class);
	AtmosphereClient client;
	Logger logger = Logger.getLogger(getClass().getName());
	Window screen;

	@Override
	public void onModuleLoad() {
		
		Button button = new Button("Broadcast");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				sendMessage();
			}
		});

		Button post = new Button("Post");
		post.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				client.post(new Event(count++,
						"This was send using the post mechanism"));
			}
		});

		Button pollButton = new Button("Poll");
		pollButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				polling.pollDelayed(3000, new AsyncCallback<Event>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to poll", caught);
					}

					@Override
					public void onSuccess(Event result) {
						Info.display(
								"Polling message received: " + result.getCode(),
								result.getData());
					}
				});
			}
		});

		Button wnd = new Button("Open Window");
		wnd.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						screen = Window.current().open(
								Document.get().getURL(),
								"child",
								new WindowFeatures().setStatus(true)
										.setResizable(true));
					}
				});
			}
		});

		Button sendWindow = new Button("Send to window");
		sendWindow.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (screen != null) {
					WindowSocket.post(screen, "wsock", "Hello Child!");
				}
			}
		});

		WindowSocket socket = new WindowSocket();
		socket.addHandler(new WindowSocket.MessageHandler() {
			@Override
			public void onMessage(String message) {
				Info.display("Received through window socket", message);
			}
		});
		socket.bind("wsock");

		initialize();

		Button killbutton = new Button("Stop");
		killbutton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				client.stop();
			}
		});

		RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();

		DockLayoutPanel panel = new DockLayoutPanel(Unit.EM);
		rootLayoutPanel.add(panel);
//		rootLayoutPanel.setWidgetLeftWidth(panel, 0.0, Unit.PX, 200.0, Unit.PX);
//		rootLayoutPanel.setWidgetTopHeight(panel, 0.0, Unit.PX, 500.0, Unit.PX);
		
		panel.addWest(getLeftBar(), 20);
	}
	
	private Widget getLeftBar() {
		DockLayoutPanel panel = new DockLayoutPanel(Unit.EM);
		panel.addNorth(getUser(), 2.0);
		panel.add(getStackedMenu());
		return panel;
	}
	
	private Widget getUser() {
		ListBox comboBox = new ListBox();
		comboBox.addItem("Dimi");
		comboBox.addItem("Mark");
		comboBox.addItem("Marzio");
		comboBox.addItem("Olga");
		comboBox.addItem("Olivier");

		comboBox.setItemSelected(2, true);
		
		comboBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				
			}
		});
		
		return comboBox;
	}
	
	private Widget getStackedMenu() {
		StackLayoutPanel stackLayoutPanel = new StackLayoutPanel(Unit.EM);
		stackLayoutPanel.setPixelSize(200, 400);
					
		stackLayoutPanel.add(new HTML(""), new HTML("Settings"), 2.0);		
		stackLayoutPanel.add(getProcedures(), new HTML("Procedures"), 2.0);
		stackLayoutPanel.add(new HTML(""), new HTML("Acquisition"), 2.0);
		stackLayoutPanel.add(new HTML(""), new HTML("2D/3D Models"), 2.0);
		stackLayoutPanel.add(new HTML(""), new HTML("Radiation Mapping"), 2.0);
		stackLayoutPanel.add(getLog(), new HTML("Log"), 2.0);	

		return  stackLayoutPanel;
	}
	
	private Widget getProcedures() {
		Tree tree = new Tree();

		TreeItem tile = new TreeItem("Tile Calo Drawer Extraction");
		tree.addItem(tile);
		
		tile.addItem("Step 1");
		tile.addItem("Step 2");
		tile.addItem("Step 3");
		tile.addItem("Step 4");
		tile.addItem("Step 5");
		tile.addItem("Step 6");
		tile.addItem("Step 7");
		tile.addItem("Step 8");
					
		TreeItem ibl = new TreeItem("IBL Installation");
		tree.addItem(ibl);

		ibl.addItem("Step A");
		ibl.addItem("Step B");
		ibl.addItem("Step C");
		ibl.addItem("Step D");

		ScrollPanel panel = new ScrollPanel();
		panel.add(tree);
		return panel;
	}
	
	private Widget getLog() {
		panel = new VerticalPanel();
		
		RadioButton error = new RadioButton("log", "Error");
		panel.add(error);
		
		RadioButton info = new RadioButton("log", "Info");
		panel.add(info);
		
		RadioButton warning = new RadioButton("log", "Warning");
		panel.add(warning);
		
		RadioButton debug = new RadioButton("log", "Debug");
		panel.add(debug);
		
		RadioButton all = new RadioButton("log", "All");
		panel.add(all);
		
		error.setValue(true);

		return new SimplePanel(panel);
	}

	private class APVSCometListener implements AtmosphereListener {

		@Override
		public void onConnected(int heartbeat, int connectionID) {
			GWT.log("comet.connected [" + heartbeat + ", " + connectionID + "]");
		}

		@Override
		public void onBeforeDisconnected() {
			logger.log(Level.INFO, "comet.beforeDisconnected");
		}

		@Override
		public void onDisconnected() {
			GWT.log("comet.disconnected");
		}

		@Override
		public void onError(Throwable exception, boolean connected) {
			int statuscode = -1;
			if (exception instanceof StatusCodeException) {
				statuscode = ((StatusCodeException) exception).getStatusCode();
			}
			GWT.log("comet.error [connected=" + connected + "] (" + statuscode
					+ ")", exception);
		}

		@Override
		public void onHeartbeat() {
			GWT.log("comet.heartbeat [" + client.getConnectionID() + "]");
		}

		@Override
		public void onRefresh() {
			GWT.log("comet.refresh [" + client.getConnectionID() + "]");
		}

		@Override
		public void onMessage(List<? extends Serializable> messages) {
			StringBuilder result = new StringBuilder();
			for (Serializable obj : messages) {
				result.append(obj.toString()).append("<br/>");
			}
			logger.log(Level.INFO, "comet.message [" + client.getConnectionID()
					+ "] " + result.toString());
			Info.display("[" + client.getConnectionID() + "] Received "
					+ messages.size() + " messages", result.toString());
			for (Serializable msg : messages) {
				if (msg instanceof TabSelectEvent) {
//					tabLayoutPanel.selectTab(((TabSelectEvent) msg).getTabNo(),
//							false);
				}
			}
		}
	}

	public void initialize() {

		APVSCometListener cometListener = new APVSCometListener();

		AtmosphereGWTSerializer serializer = GWT.create(EventSerializer.class);
		// set a small length parameter to force refreshes
		// normally you should remove the length parameter
		client = new AtmosphereClient(GWT.getModuleBaseURL() + "apvsComet",
				serializer, cometListener);
		client.start();
	}

	static int count = 0;
	private VerticalPanel panel;

	public void sendMessage() {
		client.broadcast(new Event(count++, "Button clicked!"));
	}
}
