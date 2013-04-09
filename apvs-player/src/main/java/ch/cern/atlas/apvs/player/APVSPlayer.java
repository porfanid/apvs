package ch.cern.atlas.apvs.player;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.moxieapps.gwt.highcharts.client.Chart;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public class APVSPlayer implements EntryPoint {

    static final Logger logger = Logger.getLogger(APVSPlayer.class.getName());
    
    @Override
    public void onModuleLoad() {
        
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {
                logger.log(Level.SEVERE, "Uncaught exception", e);
            }
        });


//        StockChart chart = new StockChart();
        Chart chart = new Chart();
        
        RootPanel.get().add(chart);
    
    }
    
}
