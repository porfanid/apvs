package ch.cern.atlas.apvs.player;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public class APVSPlayer implements EntryPoint {

	private static final Logger logger = Logger.getLogger(APVSPlayer.class.getName());

	@Override
	public void onModuleLoad() {

		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				logger.log(Level.SEVERE, "Uncaught exception", e);
			}
		});
		
		@SuppressWarnings("deprecation")
		long start = new Date(2013-1900, 2-1, 15).getTime();
		long end = new Date().getTime();
		
		TimePlot plot = new TimePlot();
		plot.plot(start, end, "Test");
		
		RootPanel.get("chart").add(plot);
	}
		
}
