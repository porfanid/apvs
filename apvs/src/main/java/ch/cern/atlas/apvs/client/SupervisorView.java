package ch.cern.atlas.apvs.client;

import ch.cern.atlas.apvs.eventbus.shared.RemoteEventBus;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;

public class SupervisorView extends DockPanel {

	private ScrollPanel mainScrollPanel;
	private int windowWidth;
	private int windowHeight;

	public SupervisorView(RemoteEventBus eventBus) {

		add(new Label("Atlas Procedures Visualization System"), NORTH);
		add(new Label("Version 0.1"), SOUTH);

		TabPanel tabPanel = new TabPanel();
		add(tabPanel, NORTH);

		DockPanel mainPanel = new DockPanel();
		mainPanel.add(new SupervisorWorkerView(eventBus), NORTH);
		// mainPanel.add(new SupervisorWorkerView(eventBus), NORTH);
		// FIXME add buttons
		mainScrollPanel = new ScrollPanel(mainPanel);
		tabPanel.add(mainScrollPanel, "Workers");

		tabPanel.add(new PtuView(eventBus), "PTUs");
		tabPanel.add(new DosimeterView(eventBus), "Dosimeters");
		tabPanel.add(new SupervisorSettingsView(eventBus),
				"Supervisor Settings");
		tabPanel.add(new ServerSettingsView(eventBus), "Server Settings");

		tabPanel.selectTab(0);
		
		// Save the initial size of the browser.
		windowWidth = Window.getClientWidth();
		windowHeight = Window.getClientHeight();

		// Add a listener for browser resize events.
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				// Save the new size of the browser.
				windowWidth = event.getWidth();
				windowHeight = event.getHeight();
				// Reformat everything for the new browser size.
				resize();
			}
		});

		resize();
	}

	private void resize() {
		// Set the size of main body scroll panel so that it fills the
		mainScrollPanel.setSize(
				Math.max(windowWidth - mainScrollPanel.getAbsoluteLeft(), 0)
						+ "px",
				Math.max(windowHeight - mainScrollPanel.getAbsoluteTop() - 25, 0)
						+ "px");
	}

}
